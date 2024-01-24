package com.avcoding.avmediapicker

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.avcoding.avmediapicker.model.MediaSelectionOptions
import com.avcoding.avmediapicker.utils.selectMedia

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

      //  val tv = findViewById<FrameLayout>(R.id.tv)
        val options =  MediaSelectionOptions(selectionCount =  2)
        selectMedia(R.id.tv,options){

        }
    }
}