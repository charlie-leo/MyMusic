package com.assignment.mymusic

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.assignment.mymusic.Utils.calculateDuration
import com.assignment.mymusic.Utils.truncateName
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

/**
 * Created by Charles Raj I on 06/10/24
 * @project MyMusic
 * @author Charles Raj
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MusicListScreen(
    musicViewModel: MusicViewModel,
    selectedAudio: (PlayerAction, MusicFile?) -> Unit,
    onSliderChange : (Float) -> Unit
) {

    var permission = android.Manifest.permission.READ_EXTERNAL_STORAGE

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permission = android.Manifest.permission.READ_MEDIA_AUDIO
    } else {
        permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
    }
    val mediaPermissionState = rememberPermissionState(permission = permission) { it ->
        if (it) {
            musicViewModel.setPermission()
        } else {
        }
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPermission =
            rememberPermissionState(
                permission = android.Manifest.permission.POST_NOTIFICATIONS
            ) {

            }
        LaunchedEffect(key1 = true) {
            notificationPermission.launchPermissionRequest()
        }
    }

    val scope = rememberCoroutineScope()
    val list by musicViewModel.musicList.collectAsStateWithLifecycle(initialValue = listOf())
    val playerState by musicViewModel.playerState.collectAsStateWithLifecycle()
    val musicState by musicViewModel.musicState.collectAsStateWithLifecycle()

//    var sliderState by remember {
//        mutableFloatStateOf(musicState.sliderState)
//    }

    if (mediaPermissionState.status.isGranted) {

        LaunchedEffect(key1 = true) {
            scope.launch {
                musicViewModel.loadFiles()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(list.size) { it ->
                    val music = list[it]

                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        colors = CardColors(
                            containerColor = Color.LightGray,
                            contentColor = Color.Black,
                            disabledContentColor = Color.DarkGray,
                            disabledContainerColor = Color.DarkGray
                        ),
                        onClick = {
                            selectedAudio(PlayerAction.START, music)
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            Text(
                                truncateName(music.name ?: ""),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(calculateDuration(music.duration ?: 0))
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .align(Alignment.BottomCenter)
                    .background(color = Color.LightGray)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    playerState.music?.let {
                        Text(
                            text = it.name ?: "",
                            modifier = Modifier.weight(1f)
                        )
                        Row(
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowLeft,
                                contentDescription = "",
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedAudio(PlayerAction.PREVIOUS, null)
                                    }
                            )
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "",
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedAudio(PlayerAction.PAUSE, null)
                                    }
                            )
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "",
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedAudio(PlayerAction.NEXT, null)
                                    }
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    val end =  playerState.music?.duration ?: 0
                    Slider(
                        value = musicState.sliderState,
                        onValueChange = {
//                            musicState.sliderState = it
                            musicViewModel.onAction(MusicEvent.SliderChange(it))
                            onSliderChange(musicState.sliderState)
                        },
                        valueRange = 0f..end.toFloat()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = calculateDuration(musicState.sliderState.toLong()),
                            modifier = Modifier
                        )
                        Text(
                            text = calculateDuration(end),
                            modifier = Modifier
                        )
                    }
                }
            }


        }

    } else {
        Column {
            Text("The permission is needed to process the application.")
            Button(onClick = { mediaPermissionState.launchPermissionRequest() }) {
                Text("Request Permission")
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun musicListPreview() {
//    MusicListScreen(musicViewModel = MusicViewModel())
}