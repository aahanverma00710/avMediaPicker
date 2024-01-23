package com.avcoding.avmediapicker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.avcoding.avmediapicker.databinding.FragmentAvMediaPickerBinding
import com.avcoding.avmediapicker.helper.permissionsFilter
import com.avcoding.avmediapicker.model.MediaSelectionOptions
import com.avcoding.avmediapicker.utils.ARG_PARAM_AV_MEDIA
import com.avcoding.avmediapicker.utils.AvMediaEventCallback
import com.avcoding.avmediapicker.utils.customTabs
import com.avcoding.avmediapicker.utils.hide
import com.avcoding.avmediapicker.utils.parcelable
import com.avcoding.avmediapicker.utils.show

class AvMediaPickerFragment(private val resultCallback: ((AvMediaEventCallback.Results) -> Unit)? = null) :
    Fragment(), View.OnClickListener {

    private lateinit var _binding: FragmentAvMediaPickerBinding

    private lateinit var options: MediaSelectionOptions

    private var permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                _binding.lNoPermission.clRoot.hide()
            } else {
                _binding.lNoPermission.clRoot.show()
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
        _binding = FragmentAvMediaPickerBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissions()
        setUpClicks()
    }


    private fun permissions() {
        permReqLauncher.permissionsFilter(requireActivity(), options) {
            setUpUI()
        }
    }

    private fun setUpClicks() {
        _binding.lNoPermission.btnAllow.setOnClickListener(this@AvMediaPickerFragment)
    }

    private fun setUpUI() {
        updateTabLayout()
        setUpViewPager()
    }

    override fun onClick(p0: View?) {
        when (p0) {
            _binding.lNoPermission.btnAllow -> {
                permissions()
            }
        }
    }



    private fun updateTabLayout() {
        _binding.lAllPermissionGranted.tabLayout.customTabs(options.mediaMode)
    }

    private fun setUpViewPager() {

    }

}