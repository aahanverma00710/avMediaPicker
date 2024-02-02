package com.avcoding.avmediapicker.model

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Parcelable
import androidx.annotation.Keep
import com.avcoding.avmediapicker.R
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlin.collections.ArrayList


@SuppressLint("ParcelCreator")
@Parcelize
data class Img(
    var headerDate: String = "",
    var contentUrl: Uri = Uri.EMPTY,
    var scrollerDate: String = "",
    var mediaType: Int = 1
) : Parcelable {
    @IgnoredOnParcel
    var selected = false

    @IgnoredOnParcel
    var position = 0
}

@Keep
@Parcelize
data class MediaSelectionOptions(
    var mediaMode: MediaMode = MediaMode.All,
    val selectionCount: Int = 1,
    var preSelectedUrls: ArrayList<Uri> = ArrayList(),
    var videoOptions: MediaVideoOptions = MediaVideoOptions(),
    val customSelectionOption: CustomSelectionOption = CustomSelectionOption()
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
class MediaVideoOptions : Parcelable {
    @IgnoredOnParcel
    var videoDurationLimitInSeconds = 30
}

@Parcelize
enum class MediaMode : Parcelable {
    All, Picture, Video
}

internal class ModelList(
    var list: ArrayList<Img> = ArrayList(),
    var selection: ArrayList<Img> = ArrayList()
)
@SuppressLint("ParcelCreator")
@Parcelize
data class CustomSelectionOption(
    val showToolbar: Boolean = true,
    val videoIcon : Int = R.drawable.baseline_videocam_24,
    val selectedImageIcon : Int = R.drawable.baseline_check_circle_24,
    val unSelectedImageIcon : Int = R.drawable.baseline_radio_button_unchecked_24,
    val toolbarBackIcon : Int = R.drawable.baseline_arrow_back_ios_24,
    val placeHolder : Int = R.drawable.ic_placeholder
) : Parcelable