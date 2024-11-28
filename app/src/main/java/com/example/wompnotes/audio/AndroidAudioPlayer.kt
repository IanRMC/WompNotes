package com.example.wompnotes.audio

import android.content.Context
import android.media.MediaPlayer
import java.io.File

class AndroidAudioPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun start(audioFile: File) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(audioFile.absolutePath)
            prepare()
            start()
        }
    }

}
