package com.assignment.mymusic

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

/**
 * Created by Charles Raj I on 15/10/24
 * @project MyMusic
 * @author Charles Raj
 */
class MusicApplication : Application() {

    private val CHANNEL_ID = "MusicPlayerChannel"

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Audio Service Channl",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager =getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
    }
}