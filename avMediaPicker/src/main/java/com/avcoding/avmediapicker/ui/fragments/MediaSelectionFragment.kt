package com.avcoding.avmediapicker.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import com.avcoding.avmediapicker.databinding.FragmentMediaSelectionBinding
import com.avcoding.avmediapicker.model.Img
import com.avcoding.avmediapicker.model.MediaSelectionOptions
import com.avcoding.avmediapicker.ui.MediaViewModel
import com.avcoding.avmediapicker.ui.adapter.MediaAdapter
import com.avcoding.avmediapicker.utils.ARG_PARAM_AV_MEDIA
import com.avcoding.avmediapicker.utils.ARG_PARAM_AV_MEDIA_KEY
import com.avcoding.avmediapicker.utils.LocalResourceManager
import com.avcoding.avmediapicker.utils.getMediaMode
import com.avcoding.avmediapicker.utils.hide
import com.avcoding.avmediapicker.utils.parcelable
import com.avcoding.avmediapicker.utils.show
import com.avcoding.avmediapicker.utils.updateFlaggedStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class MediaSelectionFragment : Fragment() {

    private lateinit var _binding: FragmentMediaSelectionBinding

    private lateinit var options: MediaSelectionOptions

    private lateinit var mediaAdapter: MediaAdapter

    private var scope = CoroutineScope(Dispatchers.IO)

    private var mode = 0
    private val mediaList = ArrayList<Img>()

    val vm: MediaViewModel by activityViewModels()

    var totalSelectionAllowed = 0

    companion object {
        fun getInstance(options: MediaSelectionOptions, position: Int): MediaSelectionFragment {
            val fragment = MediaSelectionFragment()
            val args = Bundle()
            args.putParcelable(ARG_PARAM_AV_MEDIA, options)
            args.putInt(ARG_PARAM_AV_MEDIA_KEY, position)
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
        setUpObserver()
    }

    private fun setUpObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm._flow.collect {
                    when (it) {
                        is MediaViewModel.Event.OnTotalItemSelected -> {
                            totalSelectionAllowed = it.count
                        }

                        else -> {

                        }
                    }
                }
            }
        }
    }

    private fun retrieveMedia() {
        if (scope.isActive) {
            scope.cancel()
        }
        scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val localResourceManager = LocalResourceManager(requireContext(),options).apply {
                this.preSelectedUrls = options.preSelectedUrls
            }
            val media = localResourceManager.retrieveMedia(mode = mode.getMediaMode())
            if (::mediaAdapter.isInitialized) {
                requireActivity().runOnUiThread {
                    mediaList.addAll(media.list)
                    mediaAdapter.updateList(mediaList, -1)
                    if (mediaList.isNotEmpty()) {
                        _binding.rvMediaSelection.show()
                        _binding.lavNoData.hide()
                        _binding.lavLoading.hide()
                    } else {
                        _binding.rvMediaSelection.hide()
                        _binding.lavNoData.show()
                        _binding.lavLoading.hide()
                    }
                }

            }
        }
    }

    private fun setUpAdapter() {
        mediaAdapter = MediaAdapter(requireActivity(), options) { callback ->
            val isForSelection = callback.selected
            if (!isForSelection && totalSelectionAllowed == options.selectionCount) {
                Toast.makeText(
                    requireContext(),
                    "You can't selected more then ${options.selectionCount} items",
                    Toast.LENGTH_SHORT
                ).show()
                return@MediaAdapter
            }
            mediaList.updateFlaggedStatus(callback.position, !isForSelection)
            mediaAdapter.updateList(mediaList, callback.position)
            if (isForSelection) {
                vm.removeItem(callback)
            } else {
                vm.updateList(callback)
            }

        }
        val mLayoutManager = GridLayoutManager(requireActivity(), 3)
        mLayoutManager.orientation = RecyclerView.VERTICAL
        mLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                Log.e("getSpanSize", "${mediaAdapter.getItemViewType(position)}")
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