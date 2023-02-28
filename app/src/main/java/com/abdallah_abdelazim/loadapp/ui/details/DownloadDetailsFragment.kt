package com.abdallah_abdelazim.loadapp.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.abdallah_abdelazim.loadapp.databinding.FragmentDownloadDetailsBinding

class DownloadDetailsFragment : Fragment() {

    private var _binding: FragmentDownloadDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDownloadDetailsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(
                DownloadDetailsFragmentDirections.actionDownloadDetailsFragmentToDownloadFragment()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        const val ARG_DOWNLOAD_STATUS = "arg_download_status"
        const val ARG_DOWNLOAD_FILE_NAME = "arg_download_file_name"

    }
}