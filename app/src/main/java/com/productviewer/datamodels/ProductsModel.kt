package com.productviewer.datamodels

import android.os.Parcel
import android.os.Parcelable

data class ProductsModel(
    val id: String?,
    val title: String?,
    val description: String?,
    val brand: String?,
    val category: String?,
    val thumbnail: String?,
    val price: Double?,
    ): Parcelable {
    // Make it Parcelable to be able to pass to next screen
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(brand)
        parcel.writeString(category)
        parcel.writeString(thumbnail)
        parcel.writeValue(price)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductsModel> {
        override fun createFromParcel(parcel: Parcel): ProductsModel {
            return ProductsModel(parcel)
        }

        override fun newArray(size: Int): Array<ProductsModel?> {
            return arrayOfNulls(size)
        }
    }
}
