package com.assignment.mymusic

import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.assignment.mymusic.screen.MusicListScreen
import com.assignment.mymusic.service.MusicService
import com.assignment.mymusic.ui.theme.MyMusicTheme

class MainActivity : ComponentActivity() {


    val musicViewModel by viewModels<MusicViewModel> ()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
         startForegroundService(Intent(this, MusicService::class.java))
        } else {
            startService(Intent(this, MusicService::class.java))
        }

        setContent {
            MyMusicTheme {
                 MusicListScreen(musicViewModel, onSelectedAudio = { musicEvent->

                     musicViewModel.onAction(musicEvent)

                     if (musicEvent is MusicEvent.SliderChange){
                         val value = musicEvent as MusicEvent.SliderChange
                         sendBroadcast(Intent(Util.SLIDER_STATE_CHANNEL).apply {
                             putExtra(Util.SLIDER_CHANGE_VALUE, value.duration)
                         })
                     } else {
                         sendBroadcast(Intent(Util.PLAYER_STATE_CHANNEL).apply {
                             putExtra("player", musicViewModel.musicState.value.playerState)
                         })
                     }
                })
            }
        }
        musicViewModel.setContentResolver(contentResolver)
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(musicViewModel.progressReceiver, IntentFilter(Util.PROGRESS_CHANNEL))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(musicViewModel.progressReceiver)
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyMusicTheme {

    }
}