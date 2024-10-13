package com.assignment.mymusic

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.provider.MediaStore.Audio.Media
import android.renderscript.RenderScript.Priority
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.URI

/**
 * Created by Charles Raj I on 07/10/24
 * @project MyMusic
 * @author Charles Raj
 */
class MusicService : Service() {

    private val CHANNEL_ID = "AudioServiceChannel"

    private lateinit var mediaPlayer: MediaPlayer
    private val scope = CoroutineScope(Dispatchers.Default)

    val playerStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("TAG", " playerStateReceiver onReceive: ")
            intent?.let {
                val playerState = it.getParcelableExtra<PlayerState>("player")
                playerState?.let {
                    playerAction(playerState)
                    Log.d("TAG", "onReceive: Music ${playerState.music}")
                    Log.d("TAG", "onReceive: Action ${playerState.actionType}")
                }
            }
        }
    }
    val sliderChange = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val value = it.getFloatExtra("slider_value", 0f)
                Log.d("TAG", "sliderChange onReceive: Slider Value ${value}")
                updateDuration(value)
            }

        }
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startNotification()

        registerReceiver(playerStateReceiver, IntentFilter(Utils.PLAYER_STATE_CHANNEL))
        registerReceiver(sliderChange, IntentFilter(Utils.SLIDER_CHANNEL))

        return START_STICKY
    }

    fun playerAction(action: PlayerState) {
        scope.launch {
            when (action.actionType) {
                PlayerAction.START -> {
                    changeMediaSource(action.music)
                }

                PlayerAction.PAUSE,
                PlayerAction.PLAY -> {
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.pause()
                        sendBroadcast(Intent(Utils.PROGRESS_CHANNEL).apply {

                            putExtra("action", "PAUSE")
                            putExtra("currentPosition", mediaPlayer.currentPosition.toLong())
                            putExtra("duration", mediaPlayer.duration.toLong())
                        })
                    } else {
                        mediaPlayer.start()
                        sendBroadcast(Intent(Utils.PROGRESS_CHANNEL).apply {
                            putExtra("action", "PLAY")
                            putExtra("currentPosition", mediaPlayer.currentPosition.toLong())
                            putExtra("duration", mediaPlayer.duration.toLong())
                        })
                    }
                }

                PlayerAction.NEXT,
                PlayerAction.PREVIOUS,
                -> {
                    changeMediaSource(action.music)
                }
            }
        }
    }

    private fun changeMediaSource(musicFile: MusicFile?){
        musicFile?.let { mus ->
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()
            mediaPlayer.setDataSource(mus.filePAth)
            mediaPlayer.isLooping = false
            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()
            }
            mediaPlayer.prepareAsync()
        }
    }
    private fun updateDuration(value : Float){
        if (value != 0f) {
            mediaPlayer.seekTo(value.toInt())
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopForeground(true)
        stopSelf()

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
        stopSelf()
        scope.cancel()
        unregisterReceiver(playerStateReceiver)
        unregisterReceiver(sliderChange)
    }


    private fun startNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Playing Audio")
            .setContentText("Your audio is playing in the background")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(false)
            .setSound(null)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
    }

}