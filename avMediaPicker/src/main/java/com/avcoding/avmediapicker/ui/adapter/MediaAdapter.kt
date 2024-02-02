package com.avcoding.avmediapicker.ui.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.avcoding.avmediapicker.R
import com.avcoding.avmediapicker.databinding.AdapterDateHeaderBinding
import com.avcoding.avmediapicker.databinding.AdapterMediaBinding
import com.avcoding.avmediapicker.model.Img
import com.avcoding.avmediapicker.model.MediaSelectionOptions
import com.avcoding.avmediapicker.utils.MediaDiffUtils
import com.avcoding.avmediapicker.utils.WIDTH
import com.avcoding.avmediapicker.utils.hide
import com.avcoding.avmediapicker.utils.show
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.util.FixedPreloadSizeProvider

class MediaAdapter(
    private val context: Context,
    private val mediaOptions: MediaSelectionOptions,
    val callback: (Img) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val HEADER = 1
        const val ITEM = 2
    }

    private val glide: RequestManager
    private val options: RequestOptions
    private var sizeProvider: ListPreloader.PreloadSizeProvider<Img>
    private val MARGIN = 4

    private val layoutParams: FrameLayout.LayoutParams

    init {
        val size: Int = WIDTH / 3 - MARGIN / 2
        layoutParams = FrameLayout.LayoutParams(size, size)
        layoutParams.setMargins(MARGIN, MARGIN - MARGIN / 2, MARGIN, MARGIN - MARGIN / 2)
        options = RequestOptions().override(size - 50)
            .format(DecodeFormat.PREFER_RGB_565)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        glide = Glide.with(context)
        sizeProvider = FixedPreloadSizeProvider(size, size)
    }

    var oldList = arrayListOf<Img>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == HEADER) {
            val view = AdapterDateHeaderBinding.inflate(layoutInflater, parent, false)
            DateViewHolder(view)
        } else {
            val view = AdapterMediaBinding.inflate(layoutInflater, parent, false)
            ItemViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return oldList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (oldList.size <= position) {
            return 0
        }
        val i = oldList[position]
        return if (i.contentUrl == Uri.EMPTY) HEADER else ITEM
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(oldList[position])
        } else if (holder is DateViewHolder) {
            holder.bind(oldList[position])
        }
    }


    fun updateList(newList: ArrayList<Img>, position: Int) {
        if (position == -1) {
            val diffResult = DiffUtil.calculateDiff(
                MediaDiffUtils(oldList, newList),
                true
            )
            oldList = ArrayList(newList)
            diffResult.dispatchUpdatesTo(this)
        } else {
            oldList = ArrayList(newList)
            notifyItemChanged(position)
        }
    }

    inner class ItemViewHolder(private val mainImageBinding: AdapterMediaBinding) :
        RecyclerView.ViewHolder(mainImageBinding.root), View.OnClickListener {
        init {
            mainImageBinding.ivIsVideo.setImageResource(mediaOptions.customSelectionOption.videoIcon)
        }

        fun bind(image: Img) {
            mainImageBinding.root.setOnClickListener(this)
            // mainImageBinding.root.setOnLongClickListener(this)
            //  mainImageBinding.mcvPreview.layoutParams = layoutParams
            try {
                glide.asBitmap()
                    .placeholder(mediaOptions.customSelectionOption.placeHolder)
                    .load(image.contentUrl)
                    .apply(options)
                    .into(mainImageBinding.ivImage)
                if (image.mediaType == 1) {
                    mainImageBinding.ivIsVideo.hide()
                } else if (image.mediaType == 3) {
                    mainImageBinding.ivIsVideo.show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val isMultiSelection = mediaOptions.selectionCount > 1
            if (isMultiSelection) {
                mainImageBinding.ivSelection.show()
            } else {
                mainImageBinding.ivSelection.hide()
            }
            val selectionOptions = mediaOptions.customSelectionOption

            if (image.selected) {
                mainImageBinding.ivSelection.setImageResource(selectionOptions.selectedImageIcon)
            } else {
                mainImageBinding.ivSelection.setImageResource(selectionOptions.unSelectedImageIcon)
            }
        }

        override fun onClick(p0: View?) {
            val position = this.layoutPosition
            val data = oldList[position]
            callback.invoke(data)
        }
    }

    inner class DateViewHolder(private val headerRowBinding: AdapterDateHeaderBinding) :
        RecyclerView.ViewHolder(headerRowBinding.root) {
        fun bind(img: Img) {
            headerRowBinding.tvDate.text = img.headerDate

        }

    }
}