package com.avcoding.avmediapicker.utils

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.avcoding.avmediapicker.model.MediaSelectionOptions
import com.avcoding.avmediapicker.ui.fragments.AvMediaPickerFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

open class AvMediaEventCallback {

    enum class Status {
        SUCCESS, BACK_PRESSED
    }

    data class Results(
        var data: List<Uri> = ArrayList(),
        var status: Status = Status.SUCCESS
    )

    private val backPressedEvents = MutableSharedFlow<Any>()
    private val outputEvents = MutableSharedFlow<Results>()

    fun onBackPressedEvent() {
        CoroutineScope(Dispatchers.IO).launch {
            backPressedEvents.emit(Any())
        }

    }


    suspend fun on(
        coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main),
        handler: suspend (Any) -> Unit
    ) = coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
        backPressedEvents.asSharedFlow().collect {
            handler(it)
        }
    }

    fun returnObjects(
        coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
        event: Results
    ) = coroutineScope.launch {
        outputEvents.emit(event)
    }


    fun results(
        coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main),
        handler: suspend (Results) -> Unit
    ) = coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
        outputEvents.asSharedFlow().collect { handler(it) }
    }
}

object AvMediaBus : AvMediaEventCallback()


fun AppCompatActivity.selectMedia(
    containerId: Int,
    options: MediaSelectionOptions?,
    resultCallback: ((AvMediaEventCallback.Results) -> Unit)? = null
) {
    supportFragmentManager.beginTransaction()
        .replace(containerId, AvMediaPickerFragment(resultCallback).apply {
            arguments = Bundle().apply {
                putParcelable(ARG_PARAM_AV_MEDIA, options)
            }
        }).commit()
}