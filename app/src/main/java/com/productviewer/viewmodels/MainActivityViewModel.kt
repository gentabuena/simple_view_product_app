package com.productviewer.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.productviewer.api.RetrofitInstance
import com.productviewer.api.RetrofitServiceInterface
import com.productviewer.datamodels.ProductModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityViewModel : ViewModel() {

    var liveDataList: MutableLiveData<ProductModel> = MutableLiveData()
    private var currentPage = 0
    private val limit = 10 // Adjust based on your requirements
    private var isLoading = false
    private var totalProducts = 0

    fun getLiveDataObserver(): MutableLiveData<ProductModel> {
        return liveDataList
    }

    fun callAPI(isLoadMore: Boolean = false) {
        if (isLoading || (isLoadMore && totalProducts <= currentPage * limit)) return
        isLoading = true

        val retrofitInstance = RetrofitInstance.getRetrofitInstance()
        val retrofitService = retrofitInstance.create(RetrofitServiceInterface::class.java)
        val call = retrofitService.getProductList(currentPage * limit, limit)
        call.enqueue(object : Callback<ProductModel> {
            override fun onResponse(call: Call<ProductModel>, response: Response<ProductModel>) {
                isLoading = false
                response.body()?.let {
                    totalProducts = it.total
                    if (isLoadMore) {
                        val currentData = liveDataList.value
                        val updatedProducts = currentData?.products?.toMutableList()
                        updatedProducts?.addAll(it.products)
                        val updatedProductModel = currentData?.copy(products = updatedProducts ?: emptyList())
                        liveDataList.postValue(updatedProductModel)
                    } else {
                        liveDataList.postValue(it)
                    }
                }
            }

            override fun onFailure(call: Call<ProductModel>, t: Throwable) {
                isLoading = false
                liveDataList.postValue(null)
            }
        })
    }

    fun loadMore() {
        currentPage++
        val skip: Int = currentPage * limit
        Log.d("TAG", "Current Page: $currentPage")
        Log.d("TAG", "Skip Value: $skip")
        Log.d("TAG", "Limit: $limit")
        callAPI(true)
    }
}