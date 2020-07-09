package com.sample.notificationvisibilityissue

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class MainActivity : AppCompatActivity() {

    private lateinit var messageBtn: Button
    private lateinit var checkVisibilityBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()
        initViews()
    }

    private fun initViews() {
        messageBtn = findViewById(R.id.message)
        checkVisibilityBtn = findViewById(R.id.check_visibility)

        messageBtn.setOnClickListener { postPrivateNotification() }
        checkVisibilityBtn.setOnClickListener { toastNotificationVisibility() }
    }

    private fun postPrivateNotification() {

        val resultPendingIntent: PendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(Intent(this@MainActivity, MainActivity::class.java))
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val publicVersionBuilder = NotificationCompat.Builder(this, PRIVATE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("1 new private message")
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setContentIntent(resultPendingIntent)


        val notification = NotificationCompat.Builder(this, PRIVATE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Jason - Private")
                .setStyle(
                        NotificationCompat.BigTextStyle()
                                .bigText("Hey this is a private message")
                )
                .setContentIntent(resultPendingIntent)
                .setPublicVersion(publicVersionBuilder.build())
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .build()

        with(NotificationManagerCompat.from(this)) {
            notify(PRIVATE_CHANNEL_ID.hashCode(), notification)
        }
    }

    private fun toastNotificationVisibility() {
        val message = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelInfo = NotificationManagerCompat.from(this)
                    .getNotificationChannel(PRIVATE_CHANNEL_ID)

            Log.d(PRIVATE_CHANNEL_ID, "Channel info : $channelInfo")
            when (val visibility = channelInfo?.lockscreenVisibility) {
                NotificationCompat.VISIBILITY_PRIVATE -> "VISIBILITY_PRIVATE"
                NotificationCompat.VISIBILITY_PUBLIC -> "VISIBILITY_PUBLIC"
                NotificationCompat.VISIBILITY_SECRET -> "VISIBILITY_SECRET"
                else -> "Value{$visibility}"
            }
        } else {
            "Notification channels not supported"
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(PRIVATE_CHANNEL_ID, PRIVATE_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            mChannel.description = PRIVATE_CHANNEL_DESC
            mChannel.lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}

const val PRIVATE_CHANNEL_ID = "message-private"
const val PRIVATE_CHANNEL_NAME = "Private messages"
const val PRIVATE_CHANNEL_DESC = "Incoming private messages"