package com.avcoding.avmediapicker.interfaces

import com.avcoding.avmediapicker.model.Img

interface OnMediaItemSelection {

    fun onItemSelectedFromImages(data:ArrayList<Img>)
    fun onItemSelectedFromVideos(data: ArrayList<Img>)
}