package com.productviewer.datamodels

data class ProductModel(
    val products: List<ProductsModel>,
    val total: Int,
    val skip: Int,
    val limit: Int
)