package com.example.wompnotes.ui.components

import android.net.Uri
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.wompnotes.data.MediaFile
import com.example.wompnotes.data.NoteTaskDatabase
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun VideoPlayer(videoUri: Uri, modifier: Modifier = Modifier.fillMaxWidth()) {
    val context = LocalContext.current
    val exoPlayer = remember {
        SimpleExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
        }
    }
    val playbackState = exoPlayer
    val isPlaying = playbackState?.isPlaying ?: false

    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer
            }
        },
        modifier = modifier.height(200.dp) // Ajusta la altura según tus necesidades
    )
}


@Composable
fun VideoPlayerWithDelete(
    videoUri: Uri,
    noteId: Int, // Agregar el ID de la nota/tarea para saber si estamos editando
    mediaFile: MediaFile, // El archivo multimedia asociado al video
    onDeleteFromList: () -> Unit, // Acción para eliminarlo de la lista temporal
    modifier: Modifier = Modifier.fillMaxWidth().padding(8.dp)
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        SimpleExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        300 // Ajusta la altura del reproductor
                    )
                }
            },
            modifier = Modifier.weight(1f) // Proporción del reproductor en el espacio disponible
        )

        IconButton(
            onClick = {
                // Eliminar de la lista temporal
                onDeleteFromList()

                // Si estamos en modo de edición, eliminar de la base de datos
                if (noteId != -1) {
                    val mediaFileDao = NoteTaskDatabase.getDatabase(context).mediaFileDao()
                    CoroutineScope(Dispatchers.IO).launch {
                        mediaFileDao.delete(mediaFile) // Eliminar de la base de datos
                    }
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar video",
                tint = Color.Red
            )
        }
    }
}

