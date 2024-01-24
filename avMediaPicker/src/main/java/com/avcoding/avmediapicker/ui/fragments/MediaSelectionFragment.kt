package com.avcoding.avmediapicker.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import com.avcoding.avmediapicker.databinding.FragmentMediaSelectionBinding
import com.avcoding.avmediapicker.model.Img
import com.avcoding.avmediapicker.model.MediaMode
import com.avcoding.avmediapicker.model.MediaSelectionOptions
import com.avcoding.avmediapicker.ui.adapter.MediaAdapter
import com.avcoding.avmediapicker.utils.ARG_PARAM_AV_MEDIA
import com.avcoding.avmediapicker.utils.ARG_PARAM_AV_MEDIA_KEY
import com.avcoding.avmediapicker.utils.LocalResourceManager
import com.avcoding.avmediapicker.utils.getMediaMode
import com.avcoding.avmediapicker.utils.parcelable
import com.avcoding.avmediapicker.utils.updateFlaggedStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class MediaSelectionFragment : Fragment() {

    private lateinit var _binding: FragmentMediaSelectionBinding

    private lateinit var options: MediaSelectionOptions

    private lateinit var mediaAdapter: MediaAdapter

    private var scope = CoroutineScope(Dispatchers.IO)

    private var mode = 0
    private val mediaList = ArrayList<Img>()
    companion object {
        fun getInstance(options: MediaSelectionOptions, position: Int): MediaSelectionFragment {
            val fragment = MediaSelectionFragment()
            val args = Bundle()
            args.putParcelable(ARG_PARAM_AV_MEDIA, options)
            args.putInt(ARG_PARAM_AV_MEDIA_KEY,position)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaList.clear()
        options = arguments?.parcelable(ARG_PARAM_AV_MEDIA) ?: MediaSelectionOptions()
        mode = arguments?.getInt(ARG_PARAM_AV_MEDIA_KEY) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaSelectionBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapter()
        retrieveMedia()
    }

    private fun retrieveMedia() {
        if (scope.isActive) {
            scope.cancel()
        }
        scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val localResourceManager = LocalResourceManager(requireContext()).apply {
                this.preSelectedUrls = options.preSelectedUrls
            }
            val media = localResourceManager.retrieveMedia(mode = mode.getMediaMode())
            if (::mediaAdapter.isInitialized) {
                requireActivity().runOnUiThread {
                    mediaList.addAll(media.list)
                    mediaAdapter.updateList(mediaList)
                }

            }
        }
    }

    private fun setUpAdapter() {
        mediaAdapter = MediaAdapter(requireActivity(),options){ callback ->
            mediaList.updateFlaggedStatus(callback.position,!callback.selected)
            mediaAdapter.updateList(mediaList)
        }
        val mLayoutManager = GridLayoutManager(requireActivity(), 3)
        mLayoutManager.orientation =  RecyclerView.VERTICAL
        mLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                Log.e("getSpanSize","${mediaAdapter.getItemViewType(position)}")
                return when (mediaAdapter.getItemViewType(position)) {
                    MediaAdapter.HEADER -> 3
                    else -> 1
                }
            }
        }
        _binding.rvMediaSelection.layoutManager = mLayoutManager
        _binding.rvMediaSelection.adapter = mediaAdapter
    }

}