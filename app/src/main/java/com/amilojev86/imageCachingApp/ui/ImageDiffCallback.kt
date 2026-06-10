package com.amilojev86.imageCachingApp.ui

import androidx.recyclerview.widget.DiffUtil
import com.amilojev86.imageCachingApp.data.ImageItem

class ImageDiffCallback(
    private val oldList: List<ImageItem>,
    private val newList: List<ImageItem>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size
    override fun areItemsTheSame(oldPos: Int, newPos: Int) =
        oldList[oldPos].id == newList[newPos].id

    override fun areContentsTheSame(oldPos: Int, newPos: Int) = true
}
