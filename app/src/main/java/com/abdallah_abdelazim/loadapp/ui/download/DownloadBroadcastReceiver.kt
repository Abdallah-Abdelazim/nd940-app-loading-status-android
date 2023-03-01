package com.abdallah_abdelazim.loadapp.ui.download

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.getSystemService

class DownloadBroadcastReceiver(
    private val onDownloadComplete: (DownloadStatus?) -> Unit
) : BroadcastReceiver() {

    var downloadId: Long = 0

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

        val action = intent.action

        if (id == downloadId && action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {

            /** Get download status **/

            val downloadManager: DownloadManager = context.getSystemService()!!

            val query = DownloadManager.Query().apply {
                setFilterById(id)
            }
            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst() && cursor.count > 0) {
                val statusColumnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                if (statusColumnIndex >= 0) {
                    val downloadStatus = cursor.getInt(statusColumnIndex)
                    onDownloadComplete(
                        if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
                            DownloadStatus.DOWNLOAD_SUCCESS
                        } else {
                            DownloadStatus.DOWNLOAD_FAILED
                        }
                    )
                    return
                }
            }

            onDownloadComplete(null)
        }
    }

    enum class DownloadStatus {
        DOWNLOAD_SUCCESS,
        DOWNLOAD_FAILED
    }

}