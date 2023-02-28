package com.abdallah_abdelazim.loadapp.ui.download

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.abdallah_abdelazim.loadapp.R
import com.abdallah_abdelazim.loadapp.databinding.FragmentDownloadBinding
import com.abdallah_abdelazim.loadapp.widget.loading_button.ButtonState

class DownloadFragment : Fragment() {

    private var _binding: FragmentDownloadBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDownloadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnDownload.setOnClickListener {
            if (binding.radioGroupDownloadOptions.checkedRadioButtonId != -1) {
                download()
                binding.btnDownload.buttonState = ButtonState.Clicked
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.err_msg_not_selected_download_method),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }

    private fun download() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}