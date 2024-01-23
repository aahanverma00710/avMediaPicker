package com.avcoding.avmediapicker.model

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlin.collections.ArrayList

@Keep
@Parcelize
data class MediaSelectionOptions(
    val mediaMode: MediaMode = MediaMode.All,
    val selectionCount: Int = 1,
    var preSelectedUrls: ArrayList<Uri> = ArrayList(),
    var videoOptions: MediaVideoOptions = MediaVideoOptions()
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
class MediaVideoOptions : Parcelable {
    @IgnoredOnParcel
    var videoDurationLimitInSeconds = 10
}

@Parcelize
enum class MediaMode : Parcelable {
    All, Picture, Video
}