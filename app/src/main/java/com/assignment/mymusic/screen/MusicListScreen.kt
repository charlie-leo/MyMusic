package com.assignment.mymusic.screen

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.assignment.mymusic.MusicEvent
import com.assignment.mymusic.MusicViewModel
import com.assignment.mymusic.R
import com.assignment.mymusic.Util
import com.assignment.mymusic.model.MusicData
import com.assignment.mymusic.service.PlayerAction
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

/**
 * Created by Charles Raj I on 13/10/24
 * @project MyMusic
 * @author Charles Raj
 */

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MusicListScreen(
    musicViewModel: MusicViewModel,
    onSelectedAudio : (musicState : MusicEvent) -> Unit
) {

    val musicList by musicViewModel.musicState.collectAsStateWithLifecycle()
//    val playerState by musicViewModel.musicList.collectAsStateWithLifecycle()


    var permission = android.Manifest.permission.READ_EXTERNAL_STORAGE

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
        permission = android.Manifest.permission.READ_MEDIA_AUDIO
    } else {
        permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val mediaPermissionState = rememberPermissionState(permission = permission){

    }

    if (mediaPermissionState.status.isGranted) {

        LaunchedEffect(key1 = true) {
            musicViewModel.loadFile()
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black)
        ) {
            Text(
                text = "My Music",
                fontSize = 20.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp)

            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(musicList.musicList.size) { index ->
                    val music = musicList.musicList[index]
                    Card(
                        onClick = {
                            onSelectedAudio( MusicEvent.Start(music))
                        },
                        colors = CardColors(
                            contentColor = Color.White,
                            containerColor = Color.Transparent,
                            disabledContentColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .padding(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(text = Util.truncateName(music.name ?: ""))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = Util.calculateDuration(music.duration ?: 0L))
                            }
                            HorizontalDivider(
                                color = Color.LightGray,
                                thickness = 1.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }

            musicList.playerState?.let { player ->
                player.music?.let {  selectedMusic ->

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max)
                            .padding(10.dp)
                            .background(
                                color = Color.LightGray.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = Util.truncateName( selectedMusic.name ?: ""),
                                fontSize = 16.sp,
                                color = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp)
                            ) {
                                Slider(
                                    value = musicList.sliderState,
                                    onValueChange = {
                                        onSelectedAudio(MusicEvent.SliderChange(it,selectedMusic))
                                    },
                                    onValueChangeFinished = {

                                    },
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color.Gray.copy(),
                                        activeTrackColor = Color.Gray.copy(),
                                        activeTickColor = Color.LightGray.copy()
                                    ),
                                    valueRange = 0f..(selectedMusic.duration ?: 0L).toFloat()
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = Util.calculateDuration( musicList.sliderState.toLong()),
                                        modifier = Modifier,
                                        fontSize = 12.sp,
                                        color = Color.White,
                                    )
                                    Text(
                                        text = Util.calculateDuration(selectedMusic.duration ?: 0L),
                                        modifier = Modifier,
                                        fontSize = 12.sp,
                                        color = Color.White,
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = Color.Transparent)
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.previous_icon),
                                    contentDescription = "",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
//                                            musicViewModel.onAction(MusicEvent.Previous(selectedMusic))
                                            onSelectedAudio( MusicEvent.Previous(selectedMusic))
                                        }
                                )

                                val actionIcon = if (
                                    player.action == PlayerAction.PAUSE){
                                    painterResource(id = R.drawable.play_icon)
                                } else {
                                    painterResource(id = R.drawable.pause_icon)
                                }

                                Box(
                                    modifier = Modifier
                                        .size(70.dp)
                                        .background(color = Color.Gray, shape = CircleShape)
                                        .clickable {
                                            if (
                                                player.action == PlayerAction.PAUSE)
                                            {
                                                onSelectedAudio( MusicEvent.Play(selectedMusic))
                                            } else {
                                                onSelectedAudio( MusicEvent.Pause(selectedMusic))
                                            }
                                        }
                                ) {
                                    Icon(
                                        painter = actionIcon,
                                        contentDescription = "",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .align(Alignment.Center)

                                    )
                                }

                                Icon(
                                    painter = painterResource(id = R.drawable.next_icon),
                                    contentDescription = "",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            onSelectedAudio( MusicEvent.Next(selectedMusic))
                                        }
                                )
                            }
                        }
                    }
                }
            }



        }
    } else {
        Column {
            Text(text = "The permission is needed to process the applicaiton")
            Button(onClick = {
                mediaPermissionState.launchPermissionRequest()
            }) {
                Text(text = "Request Permission")
            }
        }
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun MusicListPreview() {
//    MusicListScreen(musicViewModel)
}