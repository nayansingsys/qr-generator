package com.example.qrgenerator

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        if (message.notification != null) {
            sendNotification(
                message.notification!!.title!!,
                message.notification!!.body!!,
                message.notification!!.imageUrl
            )
        }
        //if(message.notification.imageUrl)
    }

    private fun sendNotification(title: String, body: String, imageUrl: Uri?) {
        val intent = Intent(this, BaseActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_btn_speak_now)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)


        if (imageUrl != null) {
            Picasso.get()
                .load(imageUrl)
                .into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        notificationBuilder.setCustomContentView(
                            getCustomDesign(
                                title,
                                body,
                                bitmap!!
                            )
                        )
                        send(notificationBuilder)
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    }

                })
        } else {
            send(notificationBuilder)
        }


    }

    private fun send(notificationBuilder: NotificationCompat.Builder) {
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            val channel = NotificationChannel(
                "channel_id",
                "Channel Name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun getCustomDesign(title: String, message: String, img: Bitmap): RemoteViews {
        val notificationLayout = RemoteViews(
            applicationContext.packageName,
            com.example.qrgenerator.R.layout.notification
        )

        notificationLayout.setTextViewText(com.example.qrgenerator.R.id.notificationTitle, title)
        notificationLayout.setTextViewText(com.example.qrgenerator.R.id.notificationBody, title)
        notificationLayout.setImageViewBitmap(com.example.qrgenerator.R.id.notificationImage, img)


        return notificationLayout
    }
}