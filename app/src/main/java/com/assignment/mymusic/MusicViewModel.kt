package com.assignment.mymusic

import android.content.ContentResolver
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Created by Charles Raj I on 06/10/24
 * @project MyMusic
 * @author Charles Raj
 */
class MusicViewModel : ViewModel() {

    private var contentResolver: ContentResolver? = null

    private val _musicList = MutableStateFlow(listOf<MusicFile>())
    val musicList : StateFlow<List<MusicFile>> = _musicList

    private val _permissionGranted = MutableStateFlow(false)
    val permissionGranted : StateFlow<Boolean> = _permissionGranted


    fun setPermission(){
        _permissionGranted.value = true
    }


    suspend fun loadFiles() = viewModelScope.launch(Dispatchers.Default){
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

        contentResolver?.let { consRes ->
            val cursor = consRes.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            )

            val musics = mutableListOf<MusicFile>()
            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val nameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val pathColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

                while (it.moveToNext()){
                    val id = it.getLong(idColumn)
                    val name = it.getString(nameColumn)
                    val duration = it.getLong(durationColumn)
                    val path = it.getString(pathColumn)

                    musics.add(MusicFile(id,name, duration,path))

                }

                _musicList.value = musics
            }


        }

    }

    fun setContentResolver(contentResolver: ContentResolver?){
        this.contentResolver = contentResolver
    }

    override fun onCleared() {
        super.onCleared()
    }

}

data class  MusicFile(
    val id: Long? = null,
    val name : String? = null,
    val duration: Long? = null,
    val filePAth: String? = null
)