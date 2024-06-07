package com.productviewer

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.productviewer.databinding.ActivityMainBinding
import com.productviewer.databinding.ActivityProductInformationBinding
import com.productviewer.datamodels.ProductsModel

class ProductInformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // View Binding
        binding = ActivityProductInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
    }

    @SuppressLint("SetTextI18n")
    private fun initViews(){
        val productData: ProductsModel? = intent.getParcelableExtra("PRODUCT_DATA")
        val productImage: ImageView = binding.productImage

        binding.backArrow.setOnClickListener {
            finish()
        }

        if (productData != null) {
            Glide.with(this)
                .load(productData.thumbnail)
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .into(productImage)
        }

        binding.productTitle.text = productData?.title

        // Setting and handling product price with bold styling for the currency symbol
        binding.productPrice.apply {
            val price = productData?.price
            if (price == null || price == 0.0) {
                visibility = View.GONE
            } else {
                val formattedPrice = "₱ $price"
                val spannable = SpannableString(formattedPrice)
                spannable.setSpan(StyleSpan(Typeface.BOLD), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                text = spannable
                visibility = View.VISIBLE
            }
        }

        binding.productDescription.apply {
            val description = productData?.description
            if (description.isNullOrEmpty()) {
                visibility = View.GONE
            } else {
                val spannable = SpannableString("Description:\n$description")
                spannable.setSpan(StyleSpan(Typeface.BOLD), 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                text = spannable
                visibility = View.VISIBLE
            }
        }

        binding.productBrand.apply {
            val brand = productData?.brand
            if (brand.isNullOrEmpty()) {
                visibility = View.GONE
            } else {
                val spannable = SpannableString("Brand: $brand")
                spannable.setSpan(StyleSpan(Typeface.BOLD), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                text = spannable
                visibility = View.VISIBLE
            }
        }

        binding.productCategory.apply {
            val category = productData?.category
            if (category.isNullOrEmpty()) {
                visibility = View.GONE
            } else {
                val spannable = SpannableString("Category: $category")
                spannable.setSpan(StyleSpan(Typeface.BOLD), 0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                text = spannable
                visibility = View.VISIBLE
            }
        }
    }

}