package com.amilojev86.imageCachingApp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.amilojev86.imageCachingApp.R
import com.amilojev86.imageCachingApp.data.ImageItem
import com.amilojev86.imageCachingApp.databinding.ItemImageBinding
import com.imageloader.SimpleImageLoader

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val items = mutableListOf<ImageItem>()

    fun submitData(newItems: List<ImageItem>) {
        val diff = DiffUtil.calculateDiff(ImageDiffCallback(items, newItems))
        items.clear()
        items.addAll(newItems)
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ImageViewHolder(
        private val binding: ItemImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ImageItem) {
            binding.textId.text = binding.root.context.getString(R.string.image_id_label, item.id)
            SimpleImageLoader.getInstance()
                .load(item.imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(binding.imageView)
        }
    }
}
