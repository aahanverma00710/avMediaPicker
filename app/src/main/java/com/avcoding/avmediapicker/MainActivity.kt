package com.avcoding.avmediapicker

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.avcoding.avmediapicker.model.MediaMode
import com.avcoding.avmediapicker.model.MediaSelectionOptions
import com.avcoding.avmediapicker.utils.AvMediaEventCallback
import com.avcoding.avmediapicker.utils.selectMedia

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val options =  MediaSelectionOptions(selectionCount =  2, mediaMode = MediaMode.Video)
        selectMedia(R.id.tv,options){
            when(it.status){
                AvMediaEventCallback.Status.SUCCESS ->{
                    val uriList = it.data
                    Log.e("selectedMediaList",uriList.size.toString())
                    removeFragment()
                }
                AvMediaEventCallback.Status.BACK_PRESSED -> {
                    removeFragment()
                }
            }
        }
    }
    private fun removeFragment(){
        if (supportFragmentManager.backStackEntryCount >0) {
            supportFragmentManager.popBackStackImmediate();
        }
    }
}