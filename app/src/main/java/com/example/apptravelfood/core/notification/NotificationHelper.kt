package com.example.apptravelfood.core.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.apptravelfood.MainActivity
import com.example.apptravelfood.R

object NotificationHelper {

    const val CHANNEL_ID = "comment_notifications"

    fun createChannel(context: Context) {
        Log.d("NotificationHelper", "Creating notification channel: $CHANNEL_ID")
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Thông báo bình luận",
            NotificationManager.IMPORTANCE_HIGH
        )

        context.getSystemService(NotificationManager::class.java)
            ?.createNotificationChannel(channel)
    }

    fun showCommentNotification(
        context: Context,
        title: String,
        message: String,
        foodStoreId: Long,
        reviewId: Long
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("foodStoreId", foodStoreId)
            putExtra("reviewId", reviewId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            reviewId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("!!!NOTI!!!", "NotificationHelper: Permission POST_NOTIFICATIONS not granted!")
            return
        }

        Log.d(
            "!!!NOTI!!!",
            "NotificationHelper: Notifying with ID: ${reviewId.toInt()} - Title: $title"
        )
        NotificationManagerCompat.from(context)
            .notify(reviewId.toInt(), notification)
    }
}