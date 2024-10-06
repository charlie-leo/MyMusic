package com.assignment.mymusic

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
fun MusicListScreen(musicViewModel: MusicViewModel) {

    val externalStoragePermissionState =
        rememberPermissionState(permission = android.Manifest.permission.READ_EXTERNAL_STORAGE)
    val mediaPermissionState =
        rememberPermissionState(permission = android.Manifest.permission.READ_MEDIA_AUDIO)

    val scope = rememberCoroutineScope()

    var isGranted = false

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        LaunchedEffect(key1 = mediaPermissionState.status.isGranted) {
            if (!mediaPermissionState.status.isGranted) {
                mediaPermissionState.launchPermissionRequest()
            } else musicViewModel.permissionStatus(true)

        }
    } else {
        LaunchedEffect(key1 = externalStoragePermissionState.status.isGranted) {
            if (!externalStoragePermissionState.status.isGranted) {
                externalStoragePermissionState.launchPermissionRequest()
            } else musicViewModel.permissionStatus(true)
        }
    }

    if (musicViewModel.permissionGranted.collectAsState(initial = false).value) {

        val musicList by musicViewModel.musicList.collectAsStateWithLifecycle(initialValue = listOf())
        LaunchedEffect(key1 = true) {
            scope.launch {
                musicViewModel.loadMusics()
            }

        }

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            LazyColumn {
                items(musicList.size){ it ->
                    val mus = musicList[it]
                    Column {
                        Text(text = mus.name ?: "")
                        Text(text = mus.duration.toString())
                        Text(text = mus.filePath ?: "")

                    }
                }
            }
        }
    } else {
        Column {

            Text("To show the music list this permission is necessary.")
            Button(onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    mediaPermissionState.launchPermissionRequest()
                } else {
                    externalStoragePermissionState.launchPermissionRequest()
                }
            }) {
                Text("Request permission")
            }
        }
    }


}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun MusicListScreenPreview() {

}