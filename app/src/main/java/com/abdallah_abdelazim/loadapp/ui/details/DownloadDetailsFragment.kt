package com.abdallah_abdelazim.loadapp.ui.details

import android.app.NotificationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.abdallah_abdelazim.loadapp.databinding.FragmentDownloadDetailsBinding
import com.abdallah_abdelazim.loadapp.ui.download.DownloadFragment

class DownloadDetailsFragment : Fragment() {

    private var _binding: FragmentDownloadDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var downloadStatus: String
    private lateinit var downloadFileName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        downloadStatus = arguments?.getString(ARG_DOWNLOAD_STATUS) ?: ""
        downloadFileName = arguments?.getString(ARG_DOWNLOAD_FILE_NAME) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDownloadDetailsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clearNotifications()
        bindDataToUi()
        setupUiListeners()
    }

    private fun bindDataToUi() {
        binding.tvFileName.text = downloadFileName
        binding.tvDownloadStatus.text = downloadStatus
    }

    private fun setupUiListeners() {

        binding.btnOk.setOnClickListener {
            findNavController().navigate(
                DownloadDetailsFragmentDirections.actionDownloadDetailsFragmentToDownloadFragment()
            )
        }
    }

    private fun clearNotifications() {
        val notificationManager = ContextCompat.getSystemService(
            requireContext(),
            NotificationManager::class.java
        )!!

        notificationManager.cancel(DownloadFragment.DOWNLOAD_NOTIFICATION_ID)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val ARG_DOWNLOAD_STATUS = "arg_download_status"
        private const val ARG_DOWNLOAD_FILE_NAME = "arg_download_file_name"

        fun createArgsBundle(downloadStatus: String, downloadFileName: String): Bundle {
            return bundleOf(
                ARG_DOWNLOAD_STATUS to downloadStatus,
                ARG_DOWNLOAD_FILE_NAME to downloadFileName
            )
        }

    }
}