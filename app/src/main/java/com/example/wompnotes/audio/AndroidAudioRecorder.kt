package com.example.wompnotes.audio

import android.content.Context
import android.media.MediaRecorder
import java.io.File

class AndroidAudioRecorder(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null

    fun start(outputFile: File) {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile.absolutePath)
            prepare()
            start()
        }
    }

    fun stop() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
    }
}
