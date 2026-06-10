package com.amilojev86.imageCachingApp.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.amilojev86.imageCachingApp.data.OkHttpImageFetcher
import com.amilojev86.imageCachingApp.databinding.ActivityMainBinding
import com.imageloader.SimpleImageLoader

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ImageViewModel by viewModels()
    private val adapter = ImageAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SimpleImageLoader.init(this, OkHttpImageFetcher())
        setupRecyclerView()
        observeViewModel()
        setupInvalidateButton()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                    binding.textError.visibility = View.GONE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.textError.visibility = View.GONE
                    adapter.submitData(state.items)
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                    binding.textError.visibility = View.VISIBLE
                    binding.textError.text = state.message
                }
            }
        }
    }

    private fun setupInvalidateButton() {
        binding.buttonInvalidateCache.setOnClickListener {
            SimpleImageLoader.getInstance().invalidateCache()
            Toast.makeText(this, "Cache cleared", Toast.LENGTH_SHORT).show()
            viewModel.loadImages()
        }
    }
}
