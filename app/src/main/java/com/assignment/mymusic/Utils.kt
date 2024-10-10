package com.assignment.mymusic

import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Created by Charles Raj I on 07/10/24
 * @project MyMusic
 * @author Charles Raj
 */


object Utils {

    const val PROGRESS_CHANNEL = "progress_channel"
    const val SLIDER_CHANNEL = "slider_channel"
    const val PLAYER_STATE_CHANNEL = "player_state_channel"

    fun calculateDuration(millis:  Long) : String{
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))

        val formattedDuration = String.format(Locale.ENGLISH,"%02d:%02d:%02d", hours, minutes, seconds)
        return formattedDuration
    }

    fun truncateName(name: String): String{
        var truncName = ""

        if (name.endsWith("mp3")){
            truncName = name.substring(startIndex = 0, endIndex = name.length - 4)
        }

        return truncName
    }

}