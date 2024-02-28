package com.avcoding.avmediapicker.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.view.WindowCompat
import com.avcoding.avmediapicker.R
import com.avcoding.avmediapicker.model.Img
import com.avcoding.avmediapicker.model.MediaMode
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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


var WIDTH = 0
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
fun View.slideUpAndShow(duration: Long = 500) {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE

        val alphaAnimator = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f)
        val translationYAnimator = ObjectAnimator.ofFloat(this, "translationY", this.height.toFloat(), 0f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(alphaAnimator, translationYAnimator)
        animatorSet.duration = duration
        animatorSet.interpolator = AccelerateInterpolator()

        // Delay the start until the view is properly laid out
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                viewTreeObserver.removeOnPreDrawListener(this)
                animatorSet.start()
                return true
            }
        })
    }
}

fun View.slideDownAndHide(duration: Long = 500) {
    if (visibility == View.VISIBLE) {
        val alphaAnimator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f)
        val translationYAnimator = ObjectAnimator.ofFloat(this, "translationY", 0f, this.height.toFloat())

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(alphaAnimator, translationYAnimator)
        animatorSet.duration = duration
        animatorSet.interpolator = AccelerateInterpolator()

        // Delay the start until the view is properly laid out
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                viewTreeObserver.removeOnPreDrawListener(this)
                animatorSet.start()
                return true
            }
        })

        animatorSet.doOnEnd {
            visibility = View.GONE
        }
    }
}
inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}


fun TabLayout.customTabs(options: MediaMode) {
    when (options) {
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

fun MediaMode.getCount(): Int {
    return when (this) {
        MediaMode.All -> {
            2
        }

        else -> {
            1
        }
    }
}

fun Resources.getDateDifference(calendar: Calendar): String {
    val d = calendar.time
    val lastMonth =
        Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -Calendar.DAY_OF_MONTH) }
    val lastWeek = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -7) }
    val recent = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -2) }
    return when {
        calendar.before(lastMonth) -> SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(d)
        calendar.after(lastMonth) && calendar.before(lastWeek) -> "Last Month"
        calendar.after(lastWeek) && calendar.before(recent) -> "Last Week"
        else -> "Recent"
    }
}

fun Activity.setupScreen() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        window.attributes.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    }*/
    getScreenSize()
}

fun Activity.getScreenSize() {
    WIDTH = DisplayMetrics().apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display?.getRealMetrics(this)
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(this)
        }
    }.widthPixels
}

fun Int.getMediaMode(): MediaMode {
    return if (this == 0) {
        MediaMode.Picture
    } else {
        MediaMode.Video
    }
}

fun ArrayList<Img>.updateFlaggedStatus(index: Int, newFlagStatus: Boolean) {
    if (index in 0 until size) {
        get(index).selected = newFlagStatus
    } else {
        throw IndexOutOfBoundsException("Index $index is out of bounds for ArrayList of size $size")
    }
}