package com.assignment.mymusic

import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong

/**
 * Created by Charles Raj I on 06/10/24
 * @project MyMusic
 * @author Charles Raj
 */

class MusicViewModel : ViewModel() {

    private var contentResolver: ContentResolver? = null

    private val _musicState = MutableStateFlow(MusicState())
    val musicState: StateFlow<MusicState> = _musicState

    private val _musicList = MutableStateFlow(listOf<MusicFile>())
    val musicList: StateFlow<List<MusicFile>> = _musicList

    private val _permissionGranted = MutableStateFlow(false)
    val permissionGranted: StateFlow<Boolean> = _permissionGranted

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState

//    private var musicService: MusicService? = null
    private var stopCounter = false

    val progressReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
        }
    }

    fun onAction(event: MusicEvent){
        when(event){
            is MusicEvent.SliderChange -> {
                _musicState.value = _musicState.value.copy(
                    sliderState = event.value
                )
            }
        }
    }


    fun setPermission() {
        _permissionGranted.value = true
    }


    suspend fun loadFiles() = viewModelScope.launch(Dispatchers.Default) {
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

                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val name = it.getString(nameColumn)
                    val duration = it.getLong(durationColumn)
                    val path = it.getString(pathColumn)

                    musics.add(MusicFile(id, name, duration, path))
                }
                _musicList.value = musics
            }
        }
    }

    fun setContentResolver(contentResolver: ContentResolver?) {
        this.contentResolver = contentResolver
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun getPlayerData(playerState: PlayerAction, musicFile: MusicFile?) : PlayerState {
        stopCounter = false
        when (playerState){
            PlayerAction.START -> {
                _playerState.value = _playerState.value.copy(
                    actionType = PlayerAction.START,
                    music = musicFile
                )
                _musicState.value = _musicState.value.copy(
                    sliderState = 0f
                )
                processCounter(musicFile?.duration)
            }
            PlayerAction.PLAY -> {
                _playerState.value = _playerState.value.copy(
                    actionType = PlayerAction.PLAY,
                )
                stopCounter = true
                processCounter(musicFile?.duration)
            }
            PlayerAction.PAUSE -> {
                _playerState.value = _playerState.value.copy(
                    actionType = PlayerAction.PAUSE,
                )
                stopCounter = true
                processCounter(musicFile?.duration)
            }
            PlayerAction.NEXT -> {
                val index = _musicList.value.indexOf(_musicList.value.filter { it.id == _playerState.value.music?.id }[0])
                Log.d("TAG", "getPlayerData: Index Next  $index")
                val next = if (index < _musicList.value.size - 1 ){
                    index + 1
                } else {
                    0
                }
                Log.d("TAG", "getPlayerData: Next  $next")
                _playerState.value = _playerState.value.copy(
                    actionType = PlayerAction.NEXT,
                    music = _musicList.value[next]
                )
                _musicState.value = _musicState.value.copy(
                    sliderState = 0f
                )
                processCounter(musicFile?.duration)
            }

            PlayerAction.PREVIOUS -> {
                val index = _musicList.value.indexOf(_musicList.value.filter { it.id == _playerState.value.music?.id }[0])
                val next = if (index <= 0){
                    _musicList.value.size - 1
                } else {
                    index - 1
                }
                Log.d("TAG", "getPlayerData: Previous  $next")
                _playerState.value = _playerState.value.copy(
                    actionType = PlayerAction.PREVIOUS,
                    music = _musicList.value[next]
                )
                _musicState.value = _musicState.value.copy(
                    sliderState = 0f
                )
                processCounter(musicFile?.duration)
            }
        }
        return _playerState.value
    }

    private fun processCounter(duration: Long?) {
        viewModelScope.launch {
            val sec = TimeUnit.MILLISECONDS.toSeconds(duration ?: 0)
            val ranSec = TimeUnit.MILLISECONDS.toSeconds(_musicState.value.sliderState.roundToLong())
            val repeatCount = sec - ranSec

            Log.d("TAG", "getPlayerData: Duration  ${duration}")
            Log.d("TAG", "getPlayerData: SEC  ${sec}")
            Log.d("TAG", "getPlayerData: RAN SEC  ${ranSec}")
            Log.d("TAG", "getPlayerData: repeat count  ${repeatCount}")

            repeat(repeatCount.toInt()){
                if (!stopCounter) {
                    delay(1000)
                    _musicState.value = _musicState.value.copy(
                        sliderState = _musicState.value.sliderState + 1000
                    )
                    Log.d("TAG", "getPlayerData: repeat count  ${_musicState.value.sliderState}")
                } else {
                    this.cancel()
                }
            }
        }
    }
    private fun stopCounter(){

    }

}

@Parcelize
data class MusicFile(
    val id: Long? = null,
    val name: String? = null,
    val duration: Long? = null,
    val filePAth: String? = null
) : Parcelable