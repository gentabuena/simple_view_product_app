package com.productviewer

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.productviewer.adapters.ProductListAdapter
import com.productviewer.databinding.ActivityMainBinding
import com.productviewer.viewmodels.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var recyclerAdapter: ProductListAdapter
    private lateinit var viewModel: MainActivityViewModel

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

        initRecyclerview()
        initViewModel()
        setupPagination()
    }

    private fun initRecyclerview() {
        recyclerAdapter = ProductListAdapter(this)
        binding.productListRecyclerview.adapter = recyclerAdapter
        binding.productListRecyclerview.layoutManager = LinearLayoutManager(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        viewModel.getLiveDataObserver().observe(this) {
            if (it !== null) {
                if (recyclerAdapter.itemCount == 0) {
                    recyclerAdapter.setProductList(it)
                } else {
                    recyclerAdapter.addProductList(it.products)
                }
                recyclerAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "Error converting result ")
            }
        }
        viewModel.callAPI()
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
}
