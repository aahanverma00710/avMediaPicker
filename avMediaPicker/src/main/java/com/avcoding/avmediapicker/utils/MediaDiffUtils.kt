package com.avcoding.avmediapicker.utils

import androidx.recyclerview.widget.DiffUtil
import com.avcoding.avmediapicker.model.Img

class MediaDiffUtils constructor(
    private val oldList: List<Img>,
    private val newList: List<Img>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].position == newList[newItemPosition].position
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].toString() == newList[newItemPosition].toString()
    }
}