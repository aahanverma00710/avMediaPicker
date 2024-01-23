package com.avcoding.avmediapicker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.avcoding.avmediapicker.databinding.FragmentMediaSelectionBinding
import com.avcoding.avmediapicker.model.MediaSelectionOptions
import com.avcoding.avmediapicker.ui.adapter.MediaAdapter
import com.avcoding.avmediapicker.utils.ARG_PARAM_AV_MEDIA
import com.avcoding.avmediapicker.utils.LocalResourceManager
import com.avcoding.avmediapicker.utils.parcelable
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

    companion object {
        fun getInstance(options: MediaSelectionOptions): MediaSelectionFragment {
            val fragment = MediaSelectionFragment()
            val args = Bundle()
            args.putParcelable(ARG_PARAM_AV_MEDIA, options)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        options = arguments?.parcelable(ARG_PARAM_AV_MEDIA) ?: MediaSelectionOptions()
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
            val media = localResourceManager.retrieveMedia()
            if (::mediaAdapter.isInitialized) {
                requireActivity().runOnUiThread {
                    mediaAdapter.updateList(media.list)
                }

            }
        }
    }

    private fun setUpAdapter() {
        mediaAdapter = MediaAdapter(requireActivity())
        _binding.rvMediaSelection.adapter = mediaAdapter
    }
}