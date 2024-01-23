package com.avcoding.avmediapicker.interfaces

import android.net.Uri

interface OnMediaSelection {

    fun onMediaSelection(data: List<Uri>)

    fun onNothingHappened()
}