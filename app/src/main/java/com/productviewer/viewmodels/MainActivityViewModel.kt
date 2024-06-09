package com.productviewer.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.productviewer.api.RetrofitInstance
import com.productviewer.api.RetrofitServiceInterface
import com.productviewer.database.AppDatabase
import com.productviewer.database.ProductEntity
import com.productviewer.datamodels.ProductModel
import com.productviewer.datamodels.ProductsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    var liveDataList: MutableLiveData<ProductModel?> = MutableLiveData()
    private var currentPage = 0
    private val limit = 10
    private var isLoading = false
    private val db: AppDatabase = AppDatabase.getDatabase(application)

    fun getLiveDataObserver(): MutableLiveData<ProductModel?> {
        return liveDataList
    }

    fun callAPI(isLoadMore: Boolean = false) {
        if (isLoading) return
        isLoading = true

        val retrofitInstance = RetrofitInstance.getRetrofitInstance()
        val retrofitService = retrofitInstance.create(RetrofitServiceInterface::class.java)
        val call = retrofitService.getProductList(currentPage * limit, limit)
        call.enqueue(object : Callback<ProductModel> {
            override fun onResponse(call: Call<ProductModel>, response: Response<ProductModel>) {
                isLoading = false
                response.body()?.let { responseBody ->
                    viewModelScope.launch(Dispatchers.IO) {
                        responseBody.products.forEach { product ->
                            val existingProduct = db.productDao().getProductById(product.id!!)
                            if (existingProduct == null) {
                                // Product doesn't exist in the database, insert it
                                val productEntity = ProductEntity(
                                    id = product.id,
                                    title = product.title ?: "",
                                    description = product.description ?: "",
                                    brand = product.brand ?: "",
                                    category = product.category ?: "",
                                    price = product.price ?: 0.0,
                                    thumbnail = product.thumbnail ?: ""
                                )
                                db.productDao().insertProduct(productEntity)
                            }
                        }
                        // After inserting new data, reload the cached products
                        loadCachedProducts(isLoadMore)
                    }
                }
            }

            override fun onFailure(call: Call<ProductModel>, t: Throwable) {
                isLoading = false
                liveDataList.postValue(null)
            }
        })
    }

    fun loadCachedProducts(isLoadMore: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            val cachedProducts = db.productDao().getAllProducts()

            if (cachedProducts.isNotEmpty()) {
                val productModel = ProductModel(
                    products = cachedProducts.map {
                        ProductsModel(
                            id = it.id,
                            title = it.title,
                            description = it.description,
                            brand = it.brand,
                            category = it.category,
                            price = it.price,
                            thumbnail = it.thumbnail
                        )
                    },
                    total = cachedProducts.size,
                    skip = 0,
                    limit = 0
                )

                if (isLoadMore) {
                    val currentData = liveDataList.value
                    val updatedProducts = currentData?.products?.toMutableList()
                    updatedProducts?.addAll(productModel.products)
                    val updatedProductModel = currentData?.copy(products = updatedProducts ?: emptyList())
                    liveDataList.postValue(updatedProductModel)
                } else {
                    liveDataList.postValue(productModel)
                }
            } else {
                // If no cached data, just load the initial data
                callAPI(isLoadMore)
            }
        }
    }

    fun loadMore() {
        currentPage++
        callAPI(true)
    }
}