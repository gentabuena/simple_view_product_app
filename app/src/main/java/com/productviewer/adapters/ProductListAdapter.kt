package com.productviewer.adapters

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.productviewer.ProductInformationActivity
import com.productviewer.R
import com.productviewer.datamodels.ProductModel
import com.productviewer.datamodels.ProductsModel

class ProductListAdapter(val activity: Activity) : RecyclerView.Adapter<ProductListAdapter.MyViewHolder>() {

    private var productList: MutableList<ProductsModel> = mutableListOf()

    fun setProductList(productModel: ProductModel) {
        this.productList.clear()
        this.productList.addAll(productModel.products)
    }

    fun addProductList(products: List<ProductsModel>) {
        this.productList.addAll(products)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_details, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val product = productList.getOrNull(position)
        product?.let { productModel ->
            holder.bind(productModel, activity)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val productLayout: LinearLayout = view.findViewById(R.id.productLayout)
        private val productImage: ImageView = view.findViewById(R.id.productImage)
        private val productTitle: TextView = view.findViewById(R.id.productTitle)
        private val productDescription: TextView = view.findViewById(R.id.productDescription)
        private val productPrice: TextView = view.findViewById(R.id.productPrice)

        fun bind(data: ProductsModel, activity: Activity) {
            Glide.with(itemView.context)
                .load(data.thumbnail)
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .into(productImage)

            productTitle.text = data.title
            productDescription.text = data.description
            productPrice.text = "₱" + data.price

            productLayout.setOnClickListener {
                val intent = Intent(activity, ProductInformationActivity::class.java)
                intent.putExtra("PRODUCT_DATA", data)
                activity.startActivity(intent)
            }
        }
    }
}