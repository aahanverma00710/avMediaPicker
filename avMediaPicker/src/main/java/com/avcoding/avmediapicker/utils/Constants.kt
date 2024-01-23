package com.avcoding.avmediapicker.utils

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.view.View
import com.avcoding.avmediapicker.model.MediaMode
import com.google.android.material.tabs.TabLayout

internal const val TAG = "AV Media logs"

internal val IMAGE_VIDEO_PROJECTION = arrayOf(
    MediaStore.Files.FileColumns._ID,
    MediaStore.Files.FileColumns.PARENT,
    MediaStore.Files.FileColumns.DISPLAY_NAME,
    MediaStore.Files.FileColumns.DATE_ADDED,
    MediaStore.Files.FileColumns.DATE_MODIFIED,
    MediaStore.Files.FileColumns.MEDIA_TYPE,
    MediaStore.Files.FileColumns.MIME_TYPE,
    MediaStore.Files.FileColumns.TITLE
)
internal const val IMAGE_VIDEO_SELECTION = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
        + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
        + " OR "
        + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
        + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
internal const val VIDEO_SELECTION = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
        + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
internal const val IMAGE_SELECTION = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
        + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
internal val IMAGE_VIDEO_URI = MediaStore.Files.getContentUri("external")!!
internal const val IMAGE_VIDEO_ORDER_BY = MediaStore.Images.Media.DATE_MODIFIED + " DESC"

const val ARG_PARAM_AV_MEDIA = "av_media"
internal const val ARG_PARAM_AV_MEDIA_KEY = "param_av_media_key"

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}


fun TabLayout.customTabs(options:MediaMode){
    when(options){
        MediaMode.All -> {
            addTab(this.newTab().setText("Images"))
            addTab(this.newTab().setText("Video"))
        }
        MediaMode.Picture -> {
            Unit
        }
        MediaMode.Video -> {
            Unit
        }
    }
}
