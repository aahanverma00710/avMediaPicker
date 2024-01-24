package com.avcoding.avmediapicker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcoding.avmediapicker.model.Img
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MediaViewModel : ViewModel() {

    val _flow = MutableSharedFlow<Event>()

    private val selectedDataList = ArrayList<Img>()
    fun updateList(data: Img) = viewModelScope.launch{
        selectedDataList.addAll(listOf(data))
        selectedDataList.distinctBy {
            it.contentUrl
        }
        val count = selectedDataList.size
        _flow.emit(Event.OnItemSelected(selectedDataList))
        _flow.emit(Event.OnTotalItemSelected(count))
    }
    fun removeItem(data:Img) = viewModelScope.launch{
        selectedDataList.remove(data)
        selectedDataList.distinctBy {
            it.contentUrl
        }
        val count = selectedDataList.size
        _flow.emit(Event.OnItemSelected(selectedDataList))
        _flow.emit(Event.OnTotalItemSelected(count))
    }
    sealed class Event{
        class OnItemSelected(val data:List<Img>) : Event()

        class OnTotalItemSelected(val count:Int) : Event()
    }

}