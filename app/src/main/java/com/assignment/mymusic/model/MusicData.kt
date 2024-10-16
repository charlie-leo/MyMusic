package com.assignment.mymusic.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Charles Raj I on 15/10/24
 * @project MyMusic
 * @author Charles Raj
 */

@Parcelize
data class MusicData(
    val id: Long? = null,
    val name: String? =null,
    val duration: Long? =null,
    val filePath: String? = null,
) : Parcelable
