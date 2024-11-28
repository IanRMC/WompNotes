package com.example.wompnotes

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class ComposeFileProvider : FileProvider() {
    companion object {
        fun getImageUri(context: Context): Uri {
            // Crear el directorio temporal
            val directory = File(context.cacheDir, "images")
            if (!directory.exists()) {
                directory.mkdirs() // Crea el directorio si no existe
            }
            // Crear un archivo temporal en el directorio
            val file = File.createTempFile("selected_image_", ".jpg", directory)
            // Obtener la URI del archivo usando FileProvider
            val authority = "${context.packageName}.fileprovider"
            return getUriForFile(context, authority, file)
        }

        fun getVideoUri(context: Context): Uri {
            val directory = File(context.cacheDir, "videos")
            directory.mkdirs()
            val file = File.createTempFile(
                "selected_video_",
                ".mp4",
                directory
            )
            val authority = "${context.packageName}.fileprovider"
            return getUriForFile(context, authority, file)
        }
    }

}
