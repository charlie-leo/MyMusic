package com.assignment.mymusic
import android.os.Build
import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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

    var permission = android.Manifest.permission.READ_EXTERNAL_STORAGE

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permission = android.Manifest.permission.READ_MEDIA_AUDIO
    } else {
        permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
    }
    val mediaPermissionState = rememberPermissionState(permission = permission){ it ->
        if (it){
            musicViewModel.setPermission()
        } else {

        }
    }
    val scope = rememberCoroutineScope()
    val list by musicViewModel.musicList.collectAsStateWithLifecycle(initialValue = listOf())

    if (mediaPermissionState.status.isGranted){

        LaunchedEffect(key1 = true) {
            scope.launch {
                musicViewModel.loadFiles()
            }
        }

        Column {
            LazyColumn {
                items(list.size){it ->
                    val music = list[it]
                    Text(music.name ?: "")
                    Text(music.duration.toString() ?: "")
                    Text(music.filePAth ?: "")
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