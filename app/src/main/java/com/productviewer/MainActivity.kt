package com.productviewer

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.productviewer.adapters.ProductListAdapter
import com.productviewer.databinding.ActivityMainBinding
import com.productviewer.datamodels.ProductModel
import com.productviewer.viewmodels.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var recyclerAdapter: ProductListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize RecyclerView
        initRecyclerView()

        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        // Observe LiveData from ViewModel
        viewModel.getLiveDataObserver().observe(this) { productModel ->
            productModel?.let { updateRecyclerView(it) }
        }

        // Check for internet connection
        if (isNetworkAvailable()) {
            // If internet is available, call API to fetch initial data
            viewModel.callAPI()
        } else {
            // If no internet, load cached data
            viewModel.loadCachedProducts()

            binding.productLoading.visibility = View.GONE
            binding.productListRecyclerview.visibility = View.VISIBLE

            // Show toast message
            // Toast.makeText(this, "No internet connection. Displaying cached data.", Toast.LENGTH_SHORT).show()
        }

        // Setup pagination
        setupPagination()
    }

    private fun initRecyclerView() {
        recyclerAdapter = ProductListAdapter(this)
        binding.productListRecyclerview.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView(productModel: ProductModel) {
        binding.productLoading.visibility = View.GONE
        binding.productListRecyclerview.visibility = View.VISIBLE
        // Update RecyclerView with new data
        recyclerAdapter.setProductList(productModel)
        recyclerAdapter.notifyDataSetChanged()
    }

    private fun setupPagination() {
        binding.productListRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                if (dy > 0) {
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        viewModel.loadMore()
                    }
                }
            }
        })
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
