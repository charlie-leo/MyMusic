package com.assignment.mymusic

import com.assignment.mymusic.model.MusicData
import com.assignment.mymusic.service.PlayerState

/**
 * Created by Charles Raj I on 15/10/24
 * @project MyMusic
 * @author Charles Raj
 */

data class MusicState(
    val musicList : MutableList<MusicData> = mutableListOf(),
    val playerState : PlayerState? = null,
    val sliderState: Float = 0f,
)