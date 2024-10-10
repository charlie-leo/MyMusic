package com.assignment.mymusic

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.assignment.mymusic.ui.theme.MyMusicTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlin.math.log

class MainActivity : ComponentActivity() {


    val musicViewModel by viewModels<MusicViewModel>()
    var selectedMusic : MusicFile? = null

//    private val serviceConnection = object : ServiceConnection {
//        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//            val serv = service as MusicService.MusicBinder
//            musicViewModel.setMusicService(serv.getService())
//            musicViewModel.setIsBound(true)
//        }
//
//        override fun onServiceDisconnected(name: ComponentName?) {
//            musicViewModel.setIsBound(false)
//        }
//
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(this, MusicService::class.java))
            } else {
                startService(Intent(this, MusicService::class.java))
            }

            MyMusicTheme {
                MusicListScreen(musicViewModel, selectedAudio = { playerState, musicFile ->
                    val state = musicViewModel.getPlayerData(playerState,musicFile)
                    sendBroadcast(Intent(Utils.PLAYER_STATE_CHANNEL).apply {
                        putExtra("player", state)
                    } )
                }, onSliderChange = { value ->
                    sendBroadcast(Intent(Utils.SLIDER_CHANNEL).apply {
                        putExtra("slider_value", value)
                    })
                })
            }
        }
        musicViewModel.setContentResolver(contentResolver)
    }

    override fun onStart() {
        super.onStart()

        registerReceiver(musicViewModel.progressReceiver, IntentFilter(Utils.PROGRESS_CHANNEL))
//        registerReceiver(musicViewModel.playerStateReceiver, IntentFilter(Utils.PLAYER_STATE_CHANNEL))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(musicViewModel.progressReceiver)
//        unregisterReceiver(musicViewModel.playerStateReceiver)
    }


}

