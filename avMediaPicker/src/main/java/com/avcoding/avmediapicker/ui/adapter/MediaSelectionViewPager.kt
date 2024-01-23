package com.avcoding.avmediapicker.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.avcoding.avmediapicker.model.MediaMode
import com.avcoding.avmediapicker.model.MediaSelectionOptions
import com.avcoding.avmediapicker.ui.fragments.MediaSelectionFragment
import com.avcoding.avmediapicker.utils.getCount

class MediaSelectionViewPager(
    fm: FragmentActivity,
    private val mediaOptions: MediaSelectionOptions
) : FragmentStateAdapter(fm) {
    override fun getItemCount(): Int {
        return mediaOptions.mediaMode.getCount()
    }

    override fun createFragment(position: Int): Fragment {
      return MediaSelectionFragment.getInstance(mediaOptions)
       /* if (mediaOptions.mediaMode != MediaMode.All) {
            return MediaSelectionFragment.getInstance(mediaOptions)
        } else {
            return if (position == 0) {
                val option = mediaOptions.apply {
                    mediaOptions.mediaMode = MediaMode.Picture
                }
                MediaSelectionFragment.getInstance(option)
            } else {
                val option = mediaOptions.apply {
                    mediaOptions.mediaMode = MediaMode.Video
                }
                MediaSelectionFragment.getInstance(option)
            }

        }*/

    }


}
