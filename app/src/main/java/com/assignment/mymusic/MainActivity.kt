package com.assignment.mymusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.assignment.mymusic.ui.theme.MyMusicTheme

class MainActivity : ComponentActivity() {


    val musicViewModel by viewModels<MusicViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            MyMusicTheme {
                MusicListScreen(musicViewModel)
            }
        }
        musicViewModel.setContentResolver(contentResolver)
    }
}

