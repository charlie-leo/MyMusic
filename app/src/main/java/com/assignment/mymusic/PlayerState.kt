package com.assignment.mymusic

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Charles Raj I on 07/10/24
 * @project MyMusic
 * @author Charles Raj
 */

@Parcelize
data class PlayerState(
    val id: Long? =null,
    var music: MusicFile? = null,
    var nextMusic: MusicFile? = null,
    var previousMusic: MusicFile? = null,
    var progress: Long? = null,
    var actionType: PlayerAction = PlayerAction.PLAY
) : Parcelable

enum class PlayerAction{
    START,
    PAUSE,
    PLAY,
    NEXT,
    PREVIOUS
}