package com.example.apptravelfood.core.notification

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.apptravelfood.R
import com.example.apptravelfood.core.di.AppContainer
import com.google.firebase.firestore.ListenerRegistration

class NotificationForegroundService : Service() {

    private var listener: ListenerRegistration? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        Log.d("!!!NOTI!!!", "NotificationForegroundService: onStartCommand started")
        NotificationHelper.createChannel(this)

        val foregroundNotification =
            NotificationCompat.Builder(
                this,
                NotificationHelper.CHANNEL_ID
            )
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("TravelFood đang chạy")
                .setContentText("Đang lắng nghe thông báo bình luận mới")
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                1001,
                foregroundNotification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(1001, foregroundNotification)
        }

        val userId = intent?.getLongExtra("userId", -1L) ?: -1L
        Log.d("!!!NOTI!!!", "NotificationForegroundService: userId from intent = $userId")

        if (userId == -1L) {
            Log.e(
                "!!!NOTI!!!",
                "NotificationForegroundService: No userId provided, stopping service"
            )
            stopSelf()
            return START_NOT_STICKY
        }

        val firebaseRepository = AppContainer.firebaseRepository

        listener?.remove()

        listener = firebaseRepository.listenUnreadNotifications(
            userId = userId
        ) { item ->
            Log.d(
                "!!!NOTI!!!",
                "NotificationForegroundService: Received item from listener: ${item.title}"
            )
            NotificationHelper.showCommentNotification(
                context = this,
                title = item.title,
                message = item.message,
                foodStoreId = item.foodStoreId,
                reviewId = item.reviewId
            )
        }

        return START_STICKY
    }

    override fun onDestroy() {
        listener?.remove()
        listener = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}