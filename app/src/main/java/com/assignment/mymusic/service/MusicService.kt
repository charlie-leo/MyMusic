package com.assignment.mymusic.service

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.assignment.mymusic.MainActivity
import com.assignment.mymusic.R
import com.assignment.mymusic.Util
import com.assignment.mymusic.model.MusicData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Created by Charles Raj I on 15/10/24
 * @project MyMusic
 * @author Charles Raj
 */
class MusicService : Service() {

    private val CHANNEL_ID = "MusicPlayerChannel"

    private lateinit var mediaPlayer: MediaPlayer
    private var scope = CoroutineScope(Dispatchers.Default)

    val playerStateReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val playerState = it.getParcelableExtra<PlayerState>(Util.PLAYER)
                playerState?.let { play ->
                    onPlayerAction(play)
                }
            }
        }
    }
    val sliderChangeReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val value = it.getFloatExtra(Util.SLIDER_CHANGE_VALUE, 0f)
                updateDuration(value)
            }
        }
    }




    override fun onCreate() {
        mediaPlayer = MediaPlayer()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startNotification()

        registerReceiver(playerStateReceiver, IntentFilter(Util.PLAYER_STATE_CHANNEL))
        registerReceiver(sliderChangeReceiver, IntentFilter(Util.SLIDER_STATE_CHANNEL))

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer.isPlaying){
            mediaPlayer.stop()
        }
        mediaPlayer.release()
        stopSelf()
        scope.cancel()
        unregisterReceiver(playerStateReceiver)
        unregisterReceiver(sliderChangeReceiver)
    }


    private fun onPlayerAction(state : PlayerState){
        scope.launch {
            when(state.action){
                PlayerAction.PLAY -> {
                    mediaPlayer.start()
                    sendBroadcast(Intent(Util.PROGRESS_CHANNEL).apply {
                        putExtra("action", "Play")
                        putExtra("currentPosition", mediaPlayer.currentPosition.toLong())
                        putExtra("duration", mediaPlayer.duration.toLong())
                    })
                }
                PlayerAction.PAUSE -> {
                    mediaPlayer.pause()
                    sendBroadcast(Intent(Util.PROGRESS_CHANNEL).apply {
                        putExtra("action", "Play")
                        putExtra("currentPosition", mediaPlayer.currentPosition.toLong())
                        putExtra("duration", mediaPlayer.duration.toLong())
                    })
                }
                PlayerAction.START,
                PlayerAction.NEXT ,
                PlayerAction.PREVIOUS -> changeMediaSource(state.music)
            }
        }
    }

    private fun changeMediaSource(mediaFile: MusicData?){
        mediaFile?.let {
            if (mediaPlayer.isPlaying){
                mediaPlayer.stop()
            }
            mediaPlayer.reset()
            mediaPlayer.setDataSource(it.filePath)
            mediaPlayer.isLooping = false
            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()
            }
            mediaPlayer.prepareAsync()
        }
    }

    private fun updateDuration(value: Float) {
        if (value != 0f){
            mediaPlayer.seekTo(value.roundToInt())
        }
    }


    private fun startNotification(){
        val notificationIntent =Intent(this, MainActivity::class.java)
        val pendingInt : PendingIntent = PendingIntent
            .getService(this,0,notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Playing Audio")
            .setContentText(" You audio is playing in the background")
            .setSmallIcon(R.drawable.play_icon)
            .setContentIntent(pendingInt)
            .build()
        startForeground(1,notification)
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}