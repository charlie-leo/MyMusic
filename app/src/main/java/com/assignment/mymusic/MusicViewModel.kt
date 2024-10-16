package com.assignment.mymusic

import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment.mymusic.model.MusicData
import com.assignment.mymusic.service.PlayerAction
import com.assignment.mymusic.service.PlayerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong

/**
 * Created by Charles Raj I on 15/10/24
 * @project MyMusic
 * @author Charles Raj
 */
class MusicViewModel : ViewModel() {

    private var contentResolver: ContentResolver? = null

    private val _musicState = MutableStateFlow(MusicState())
    val musicState: StateFlow<MusicState> = _musicState

    private var stopCounter = false

    private val mutex = Mutex()

    val progressReceiver = object :  BroadcastReceiver(){

        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let { data ->
//                putExtra("currentPosition", mediaPlayer.currentPosition.toLong())
//                putExtra("duration", mediaPlayer.duration.toLong())
                val currentPosition = data.getLongExtra("currentPosition",0)
                val duration = data.getLongExtra("duration",0)

                _musicState.value = _musicState.value.copy(
                    sliderState = currentPosition.toFloat(),
                )
//                stopCounter = true
//                processCounter()
            }
        }

    }


    fun setContentResolver(contentResolver: ContentResolver) {
        this.contentResolver = contentResolver
    }


    fun onAction(event: MusicEvent) {
        when (event) {
            is MusicEvent.Next -> onNext()
            is MusicEvent.Pause -> onPause()
            is MusicEvent.Play -> onPlay()
            is MusicEvent.Previous -> onPrevious()
            is MusicEvent.SliderChange -> onSliderChange(event.duration)
            is MusicEvent.Start -> onStart(event.musicFile)
        }
    }

    private fun onStart(musicFile: MusicData) {
        val playerState = PlayerState(
            music = musicFile,
            action = PlayerAction.START,
            progress = 0L
        )
        _musicState.value = _musicState.value.copy(
            playerState = playerState,
            sliderState = 0f
        )
        stopCounter = true
        processCounter()
    }

    private fun onNext() {
        val currentMusic = _musicState.value.playerState?.music
        val index = _musicState.value.musicList.indexOf(
            _musicState.value.musicList.filter {
                it.id == currentMusic?.id
            }[0]
        )

        val nextIndex = if (index < _musicState.value.musicList.size - 1) {
            index + 1
        } else {
            0
        }

        val playerState = _musicState.value.playerState?.copy(
            action = PlayerAction.NEXT,
            music = _musicState.value.musicList[nextIndex]
        )
        _musicState.value = _musicState.value.copy(
            playerState = playerState,
            sliderState = 0f
        )
        stopCounter = true
        processCounter()
    }

    private fun onPrevious() {

        val currentMusic = _musicState.value.playerState?.music
        val index = _musicState.value.musicList.indexOf(
            _musicState.value.musicList.filter {
                it.id == currentMusic?.id
            }[0]
        )

        val previousIndex = if (index <= 0) {
            _musicState.value.musicList.size - 1
        } else {
            index - 1
        }

        val playerState = _musicState.value.playerState?.copy(
            action = PlayerAction.PREVIOUS,
            music = _musicState.value.musicList[previousIndex]
        )
        _musicState.value = _musicState.value.copy(
            playerState = playerState,
            sliderState = 0f
        )
        stopCounter = true
        processCounter()

    }

    private fun onPause() {
        val playerState = _musicState.value.playerState ?: PlayerState()
        playerState.action = PlayerAction.PAUSE
        _musicState.value = _musicState.value.copy(
            playerState = playerState
        )
        stopCounter = true
    }

    private fun onPlay() {
        val playerState = _musicState.value.playerState ?: PlayerState()
        playerState.action = PlayerAction.PLAY
        _musicState.value = _musicState.value.copy(
            playerState = playerState
        )
        stopCounter = true
        processCounter()
    }

    private fun onSliderChange(sliderState: Float) {
        _musicState.value = _musicState.value.copy(
            sliderState = sliderState
        )
        stopCounter = true
        processCounter()
    }

    /*
    * 1.  Thead 1 ---> started 300
    * 2.  Thead 2  ---->  started 240
    * 3.  Thead 3  ---->  stop counter
    * */


    private fun processCounter() {
        viewModelScope.launch {
            mutex.withLock {
                val playerState = _musicState.value.playerState
                val sec = TimeUnit.MILLISECONDS.toSeconds(playerState?.music?.duration ?: 0)
                val ranSec =
                    TimeUnit.MILLISECONDS.toSeconds(_musicState.value.sliderState.roundToLong())
                val repeatCount = sec - ranSec


                if (repeatCount <= 0) {
                    stopCounter = true
                    return@launch
                }
                stopCounter = false

                for (i in 0 until repeatCount.toInt()) {
                    if (stopCounter) {
                        break
                    }
                    delay(1000)
                    _musicState.value = _musicState.value.copy(
                        sliderState = _musicState.value.sliderState + 1000
                    )
                }
            }
        }
    }


    fun loadFile() = viewModelScope.launch(Dispatchers.Default) {
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"


        contentResolver?.let { cosRes ->


            val cursor = cosRes.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            )
            val musics = mutableListOf<MusicData>()

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

                    musics.add(MusicData(id, name, duration, path))
                }
                _musicState.value = _musicState.value.copy(
                    musicList = musics
                )
            }

        }

    }

}