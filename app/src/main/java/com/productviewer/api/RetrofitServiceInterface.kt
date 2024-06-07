package com.productviewer.api

import com.productviewer.datamodels.ProductModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitServiceInterface {

    @GET("products")
    fun getProductList(): Call<ProductModel>

    @GET("products")
    fun getProductList(
        @Query("skip") skip: Int,
        @Query("limit") limit: Int
    ): Call<ProductModel>
}