package com.assignment.mymusic.service

import android.os.Parcelable
import com.assignment.mymusic.model.MusicData
import kotlinx.parcelize.Parcelize

/**
 * Created by Charles Raj I on 15/10/24
 * @project MyMusic
 * @author Charles Raj
 */
@Parcelize
data class PlayerState(
    val id : Long? = null,
    val music: MusicData? = null,
    var progress: Long? = null,
    var action: PlayerAction = PlayerAction.START
) : Parcelable

enum class PlayerAction{
    START,
    PLAY,
    PAUSE,
    NEXT,
    PREVIOUS
}
