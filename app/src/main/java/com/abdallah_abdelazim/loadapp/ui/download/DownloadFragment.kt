package com.abdallah_abdelazim.loadapp.ui.download

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkBuilder
import com.abdallah_abdelazim.loadapp.R
import com.abdallah_abdelazim.loadapp.databinding.FragmentDownloadBinding
import com.abdallah_abdelazim.loadapp.ui.details.DownloadDetailsFragment
import com.abdallah_abdelazim.loadapp.ui.download.DownloadBroadcastReceiver.DownloadStatus
import com.abdallah_abdelazim.loadapp.widget.loading_button.ButtonState
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class DownloadFragment : Fragment() {

    private var _binding: FragmentDownloadBinding? = null
    private val binding get() = _binding!!

    private var currentDownloadFileOptionName: String = ""

    private lateinit var notificationManager: NotificationManager

    private val receiver = DownloadBroadcastReceiver(::handleCompletedDownload)

    private fun handleCompletedDownload(downloadStatus: DownloadStatus?) {

        binding.btnDownload.buttonState = ButtonState.Completed

        sendDownloadNotificationWithPermissionCheck(downloadStatus)

        currentDownloadFileOptionName = ""
        receiver.downloadId = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notificationManager = getSystemService(requireContext(), NotificationManager::class.java)!!

        createDownloadNotificationChannel()

        registerReceiver(
            requireContext(),
            receiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            RECEIVER_EXPORTED
        )
    }

    private fun createDownloadNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                getString(R.string.notification_channel_id),
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDownloadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUiListeners()
    }

    private fun setupUiListeners() {
        binding.btnDownload.setOnClickListener {
            if (binding.radioGroupDownloadOptions.checkedRadioButtonId != -1) {
                download()
                binding.btnDownload.buttonState = ButtonState.Clicked
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.err_msg_not_selected_download_file),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun mapDownloadFileOptionToUrl(@IdRes checkedRadioButtonId: Int): String {
        return when (checkedRadioButtonId) {
            R.id.radio_button_glide -> GLIDE_REPO_URL
            R.id.radio_button_load_app -> LOAD_APP_STARTER_REPO_URL
            R.id.radio_button_retrofit -> RETROFIT_REPO_URL
            else -> error("Checked radio button doesn't map to a download URL!")
        }
    }

    private fun download() {
        val checkedRadioButtonId = binding.radioGroupDownloadOptions.checkedRadioButtonId
        if (checkedRadioButtonId >= 0) {
            val downloadUrl = mapDownloadFileOptionToUrl(checkedRadioButtonId)

            val request =
                DownloadManager.Request(Uri.parse(downloadUrl))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                request.setRequiresCharging(false)
            }

            val downloadManager =
                getSystemService(requireContext(), DownloadManager::class.java)!!

            receiver.downloadId = downloadManager.enqueue(request)

            currentDownloadFileOptionName =
                (binding.root.findViewById(checkedRadioButtonId) as RadioButton).text as String
        }
    }

    @SuppressLint("InlinedApi")
    @NeedsPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun sendDownloadNotification(downloadStatus: DownloadStatus?) {

        val status = when (downloadStatus) {
            DownloadStatus.DOWNLOAD_SUCCESS -> getString(R.string.download_status_success)
            DownloadStatus.DOWNLOAD_FAILED -> getString(R.string.download_status_failed)
            else -> getString(R.string.download_status_unknown)
        }

        val args = bundleOf(
            DownloadDetailsFragment.ARG_DOWNLOAD_STATUS to status,
            DownloadDetailsFragment.ARG_DOWNLOAD_FILE_NAME to currentDownloadFileOptionName
        )

        val pendingIntent = NavDeepLinkBuilder(requireContext())
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.DownloadDetailsFragment)
            .setArguments(args)
            .createPendingIntent()

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(
            requireContext(),
            getString(R.string.notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_cloud_download)
            .setContentTitle(getString(R.string.download_notification_title))
            .setContentText(currentDownloadFileOptionName)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(0, getString(R.string.download_notification_button_text), pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(DOWNLOAD_NOTIFICATION_ID, notificationBuilder.build())
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        context?.unregisterReceiver(receiver)
        super.onDestroy()
    }

    companion object {

        const val DOWNLOAD_NOTIFICATION_ID = 0

        private const val LOAD_APP_STARTER_REPO_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"
        private const val GLIDE_REPO_URL =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val RETROFIT_REPO_URL =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"

    }
}