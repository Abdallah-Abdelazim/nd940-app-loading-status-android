package com.abdallah_abdelazim.loadapp.ui.download

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
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
import androidx.fragment.app.Fragment
import com.abdallah_abdelazim.loadapp.R
import com.abdallah_abdelazim.loadapp.databinding.FragmentDownloadBinding
import com.abdallah_abdelazim.loadapp.widget.loading_button.ButtonState

class DownloadFragment : Fragment() {

    private var _binding: FragmentDownloadBinding? = null
    private val binding get() = _binding!!

    private var downloadID: Long = 0
    private var downloadFileOptionName: String = ""

    private lateinit var downloadManager: DownloadManager
    private lateinit var notificationManager: NotificationManager

    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val action = intent?.action

            if (id == downloadID && action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {

                // Get download status

                val query = DownloadManager.Query()
                query.setFilterById(id)
                val cursor = downloadManager.query(query)

                if (cursor.moveToFirst() && cursor.count > 0) {

                    val statusColumnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    if (statusColumnIndex >= 0) {
                        val downloadStatus = cursor.getInt(statusColumnIndex)

                        if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
                            sendDownloadNotification(
                                getString(R.string.download_status_success),
                                downloadFileOptionName
                            )
                        } else {
                            sendDownloadNotification(
                                getString(R.string.download_status_failed),
                                downloadFileOptionName
                            )
                        }
                    }
                }

                binding.btnDownload.buttonState = ButtonState.Completed
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        downloadManager = getSystemService(requireContext(), DownloadManager::class.java)!!
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
            notificationChannel.enableVibration(true)

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
            val checkedRadioButtonId = binding.radioGroupDownloadOptions.checkedRadioButtonId
            if (checkedRadioButtonId != -1) {
                download(mapDownloadFileOptionToUrl(checkedRadioButtonId))
                binding.btnDownload.buttonState = ButtonState.Clicked
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.err_msg_not_selected_download_method),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        binding.radioGroupDownloadOptions.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId != -1) {
                downloadFileOptionName =
                    (binding.root.findViewById(checkedId) as RadioButton).text as String
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

    private fun download(downloadUrl: String) {
        val request =
            DownloadManager.Request(Uri.parse(downloadUrl))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            request.setRequiresCharging(false)
        }

        val downloadManager =
            getSystemService(requireContext(), DownloadManager::class.java)!!

        downloadID = downloadManager.enqueue(request)
    }

    private fun sendDownloadNotification(msg: String, fileName: String) {

        // TODO
//        val intent = Intent(context, DetailActivity::class.java)
//        intent.putExtra("status", msg)
//        intent.putExtra("filename", fileName)
//
//        val pendingIntent = PendingIntent.getActivity(
//            context,
//            NOTIFICATION_ID,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )
//
//        val cloudImage = BitmapFactory.decodeResource(context.resources, R.drawable.cloud_download)
//        val bigPicStyle = NotificationCompat.BigPictureStyle()
//            .bigPicture(cloudImage)
//            .bigLargeIcon(null)
//
//        // Build the notification
//        val builder =
//            NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
//        builder.setSmallIcon(R.drawable.cloud_download)
//            .setContentTitle(context.getString(R.string.app_name))
//            .setContentText(msg)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//            .addAction(0, context.getString(R.string.see_details), pendingIntent)
//            .setStyle(bigPicStyle)
//            .setLargeIcon(cloudImage)
//            .priority = NotificationCompat.PRIORITY_HIGH
//
//        notify(NOTIFICATION_ID, builder.build())
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

        private const val LOAD_APP_STARTER_REPO_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"
        private const val GLIDE_REPO_URL =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val RETROFIT_REPO_URL =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"

    }
}