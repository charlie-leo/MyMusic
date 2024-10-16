package com.assignment.mymusic

import com.assignment.mymusic.model.MusicData

/**
 * Created by Charles Raj I on 15/10/24
 * @project MyMusic
 * @author Charles Raj
 */

sealed interface MusicEvent {

    data class Start(val musicFile : MusicData) : MusicEvent
    data class Play(val musicFile : MusicData) : MusicEvent
    data class Pause(val musicFile : MusicData) : MusicEvent
    data class Next(val musicFile : MusicData) : MusicEvent
    data class Previous(val musicFile : MusicData) : MusicEvent
    data class SliderChange(val duration : Float, val musicFile : MusicData) : MusicEvent

}