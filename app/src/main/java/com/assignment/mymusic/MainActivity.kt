package com.assignment.mymusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyMusicTheme {
        Greeting("Android")
    }
}