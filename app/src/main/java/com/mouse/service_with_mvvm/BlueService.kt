package com.mouse.service_with_mvvm

import android.app.*
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class BlueService : Service() {
    lateinit var blueUtil:IBlueUtil
    val blueBinder: IBinder = BlueBinder()
    override fun onCreate() {
        super.onCreate()
        blueUtil=BlueSelfUtil(applicationContext)
        createNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }
    lateinit var notificationManager: NotificationManager
    lateinit var builder: NotificationCompat.Builder
    private fun createNotification() { //TODO
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, MainActivity::class.java)
        lateinit var pendingIntent: PendingIntent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            //setChannelId 要寫且要指定要使用哪個channel的id 否則不會顯示notification
            builder = NotificationCompat.Builder(this, "100")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("藍芽服務運行中")
                .setTicker("藍芽服務運行中")
//                .setContentText("來電服務啟用中")
                .setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                )
//                .setVibrate(
//                    longArrayOf(300, 600, 300, 600)
//                )
//                .setLights(Color.GREEN, 1000, 1000).setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setChannelId("wfe_藍芽")
            val channel =
                NotificationChannel("wfe_藍芽", "藍芽", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        } else {
            val notification = NotificationCompat.Builder(this, "100")
            builder =
                notification
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("藍芽服務運行中")
                    .setTicker("藍芽服務運行中")
//                    .setContentText("my text")
        }
        val nofication = builder.build()
        startForeground(1, nofication)
    }
    override fun onBind(p0: Intent?): IBinder? {
        return blueBinder
    }
    suspend fun scan() :Flow<String> = blueUtil.scan()


    inner class BlueBinder : Binder() {
        fun getService(): BlueService {
            return this@BlueService
        }
    }
}