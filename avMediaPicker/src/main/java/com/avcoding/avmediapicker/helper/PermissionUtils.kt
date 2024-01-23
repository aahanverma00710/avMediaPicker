package com.avcoding.avmediapicker.helper

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.avcoding.avmediapicker.model.MediaMode
import com.avcoding.avmediapicker.model.MediaSelectionOptions

private val REQUIRED_PERMISSIONS_IMAGES =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    } else {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
private val REQUIRED_PERMISSIONS_VIDEO =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO
        )
    } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    } else {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

fun ActivityResultLauncher<Array<String>>.permissionsFilter(
    fragmentActivity: FragmentActivity,
    options: MediaSelectionOptions,
    callback: () -> Unit
) {
    if (fragmentActivity.allPermissionsGranted(options.mediaMode)) {
        callback()
    } else {
        this.launch(if (options.mediaMode == MediaMode.Picture) REQUIRED_PERMISSIONS_IMAGES else REQUIRED_PERMISSIONS_VIDEO)
    }
}

private fun Activity.allPermissionsGranted(mode: MediaMode) =
    (if (mode == MediaMode.Picture) REQUIRED_PERMISSIONS_IMAGES else REQUIRED_PERMISSIONS_VIDEO).all {
        val check = ContextCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
        check
    }