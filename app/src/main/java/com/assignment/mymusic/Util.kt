package com.assignment.mymusic

import java.util.Locale
import java.util.concurrent.TimeUnit


/**
 * Created by Charles Raj I on 15/10/24
 * @project MyMusic
 * @author Charles Raj
 */


object Util{

    const val PLAYER = "player"
    const val SLIDER_CHANGE_VALUE = "slider_change_value"
    const val PLAYER_STATE_CHANNEL = "playerState_value"
    const val SLIDER_STATE_CHANNEL = "slider_state_value"
    const val PROGRESS_CHANNEL = "progress_channel"

    fun calculateDuration(milli : Long) : String{
        val hours = TimeUnit.MILLISECONDS.toHours(milli)
        val minuts = TimeUnit.MILLISECONDS.toMinutes(milli) - TimeUnit.HOURS.toMinutes(hours)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milli) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milli))

        val formattedDuration = String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours,minuts,seconds)
        return formattedDuration

    }

    fun truncateName(name: String) : String {
        var truncName = ""
        if (name.endsWith("mp3")){
            truncName = name.substring(startIndex = 0, endIndex = name.length - 4)
        }
        return truncName
    }

}