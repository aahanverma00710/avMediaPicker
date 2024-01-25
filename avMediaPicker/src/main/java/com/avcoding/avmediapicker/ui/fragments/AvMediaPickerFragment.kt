package com.avcoding.avmediapicker.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.avcoding.avmediapicker.databinding.FragmentAvMediaPickerBinding
import com.avcoding.avmediapicker.helper.permissionsFilter
import com.avcoding.avmediapicker.model.MediaMode
import com.avcoding.avmediapicker.model.MediaSelectionOptions
import com.avcoding.avmediapicker.ui.MediaViewModel
import com.avcoding.avmediapicker.ui.adapter.MediaSelectionViewPager
import com.avcoding.avmediapicker.utils.ARG_PARAM_AV_MEDIA
import com.avcoding.avmediapicker.utils.AvMediaEventCallback
import com.avcoding.avmediapicker.utils.hide
import com.avcoding.avmediapicker.utils.parcelable
import com.avcoding.avmediapicker.utils.setupScreen
import com.avcoding.avmediapicker.utils.show
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AvMediaPickerFragment(private val resultCallback: ((AvMediaEventCallback.Results) -> Unit)? = null) :
    Fragment(), View.OnClickListener, TabLayout.OnTabSelectedListener {

    private lateinit var _binding: FragmentAvMediaPickerBinding

    private lateinit var options: MediaSelectionOptions

    private lateinit var vpAdapter: MediaSelectionViewPager

    val vm : MediaViewModel by activityViewModels()

    private var permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                _binding.lNoPermission.clRoot.hide()
                setUpUI()
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
        setUpObservers()

    }

    private fun setUpObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                vm._flow.collect{
                    when(it){
                        is MediaViewModel.Event.OnItemSelected -> {
                            Log.e("setUpObservers", "${it.data.size}")
                            if (options.selectionCount == 1){
                                handleSuccess()
                            }else{
                                if (it.data.isEmpty()){
                                    _binding.btnSelectionCount.hide()
                                }else{
                                    _binding.btnSelectionCount.show()
                                    _binding.btnSelectionCount.text = "Selected (${it.data.size})"
                                }
                            }
                        }
                        is MediaViewModel.Event.OnTotalItemSelected ->{
                            Log.e("setUpObservers", "${it.count}")
                        }
                    }
                }
            }
        }
    }


    private fun permissions() {
        permReqLauncher.permissionsFilter(requireActivity(), options) {
            setUpUI()
        }
    }

    private fun setUpClicks() {
        _binding.lNoPermission.btnAllow.setOnClickListener(this@AvMediaPickerFragment)
        _binding.ivBack.setOnClickListener(this@AvMediaPickerFragment)
        _binding.btnSelectionCount.setOnClickListener(this@AvMediaPickerFragment)
    }

    private fun setUpUI() {
        setUpViewPager()
    }

    override fun onClick(p0: View?) {
        when (p0) {
            _binding.lNoPermission.btnAllow -> {
                permissions()
            }
            _binding.ivBack ->{
                resultCallback?.invoke(AvMediaEventCallback.Results(status = AvMediaEventCallback.Status.BACK_PRESSED))
            }
            _binding.btnSelectionCount ->{
                handleSuccess()
            }
        }
    }

    private fun handleSuccess() {
        val selectionList = vm.getSelectedMediaList()
        resultCallback?.invoke(AvMediaEventCallback.Results(data = selectionList, status = AvMediaEventCallback.Status.SUCCESS))
    }


    private fun setUpViewPager() {
        vpAdapter = MediaSelectionViewPager(requireActivity(), options)
        _binding.lAllPermissionGranted.vpItems.adapter = vpAdapter
        if (options.mediaMode == MediaMode.All) {
            _binding.lAllPermissionGranted.tabLayout.show()
            TabLayoutMediator(
                _binding.lAllPermissionGranted.tabLayout,
                _binding.lAllPermissionGranted.vpItems
            ) { tab, position ->

                if (position == 0) {
                    tab.text = "Images"
                } else {
                    tab.text = "Video"
                }

            }.attach()
        }else{
            _binding.lAllPermissionGranted.tabLayout.hide()
        }
        _binding.lAllPermissionGranted.tabLayout.addOnTabSelectedListener(this@AvMediaPickerFragment)
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tab?.position == 0){
            _binding.lAllPermissionGranted.vpItems.setCurrentItem(0,true)
        }else{
            _binding.lAllPermissionGranted.vpItems.setCurrentItem(1,true)
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

}