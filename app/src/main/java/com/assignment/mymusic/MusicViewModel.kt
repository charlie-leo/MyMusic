package com.assignment.mymusic

import android.content.ContentResolver
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * Created by Charles Raj I on 06/10/24
 * @project MyMusic
 * @author Charles Raj
 */


class MusicViewModel : ViewModel() {


    private var contentResolver: ContentResolver? = null

    private val _musicList = MutableStateFlow(listOf<MusicFile>())
    val musicList : SharedFlow<List<MusicFile>> = _musicList

    private val _permissionGranted = MutableStateFlow(false)
    val permissionGranted : SharedFlow<Boolean> = _permissionGranted

    fun permissionStatus(value : Boolean){
        _permissionGranted.value = value
    }

    suspend fun loadMusics() = viewModelScope.launch(Dispatchers.Default) {

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0" // Get only music files

        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

        contentResolver?.let { contentREs ->
            val cursor = contentREs.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            )

            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val displayNameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

                val musics = mutableListOf<MusicFile>()
                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val displayName = it.getString(displayNameColumn)
                    val duration = it.getLong(durationColumn)
                    val data = it.getString(dataColumn)

                    val audioFile = MusicFile(id, displayName, duration, data)
                    musics.add(audioFile)
                }
                _musicList.value = musics
            }
        }


    }

    override fun onCleared() {
        super.onCleared()
    }

    fun setContentResolver(contentResolver: ContentResolver?) {
        contentResolver?.let {
            this.contentResolver = contentResolver
        }
    }
}

data class MusicFile(
    val id: Long? = null,
    val name: String? = null,
    val duration: Long? = null,
    val filePath: String? = null
)