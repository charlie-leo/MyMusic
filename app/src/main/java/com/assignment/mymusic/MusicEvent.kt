package com.assignment.mymusic

/**
 * Created by Charles Raj I on 09/10/24
 * @project MyMusic
 * @author Charles Raj
 */
sealed interface MusicEvent {
    data class SliderChange(val value : Float) : MusicEvent
}