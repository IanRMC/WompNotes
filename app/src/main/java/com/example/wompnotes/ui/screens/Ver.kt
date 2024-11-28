package com.example.wompnotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.wompnotes.ViewModel.NoteTaskViewModel
import com.example.wompnotes.data.MediaFile
import androidx.compose.foundation.lazy.items
import coil.compose.AsyncImage
import com.example.wompnotes.ui.components.VideoPlayerWithDelete
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.ui.window.Dialog
import com.example.wompnotes.audio.AndroidAudioPlayer
import com.example.wompnotes.data.NoteTaskDatabase
import com.example.wompnotes.ui.components.VideoPlayer
import java.io.File

@Composable
fun VerPantalla(
    navController: NavController,
    noteId: Int,
    viewModel: NoteTaskViewModel = viewModel()
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    var selectedVideoUri by remember { mutableStateOf<String?>(null) } // URI del video para maximizar
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val borderColor = if (isDarkTheme) Color.Gray else Color.LightGray

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var mediaFiles by remember { mutableStateOf(emptyList<MediaFile>()) }
    val player = remember { AndroidAudioPlayer(context) }

    // Cargar los datos de la nota o tarea según el ID
    LaunchedEffect(noteId) {
        if (noteId == -1) {
            navController.popBackStack()
            return@LaunchedEffect
        }

        val note = viewModel.notesTasks.value.find { it.id == noteId }
        if (note == null) {
            navController.popBackStack()
        } else {
            title = note.title
            description = note.description
            selectedTime = note.date ?: "Fecha no especificada"
            type = note.type

            val mediaFileDao = NoteTaskDatabase.getDatabase(context).mediaFileDao()
            mediaFiles = mediaFileDao.getMediaFilesForNote(noteId)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Atrás", color = textColor)
                }
                Text(
                    text = title.ifEmpty { "Cargando..." },
                    style = MaterialTheme.typography.titleLarge.copy(color = textColor)
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(text = "Título", fontSize = 14.sp, color = textColor, fontWeight = FontWeight.Bold)
                Text(text = title, color = textColor, modifier = Modifier.padding(vertical = 8.dp))

                if (type == "Tarea") {
                    Text(text = "Hora", fontSize = 14.sp, color = textColor, fontWeight = FontWeight.Bold)
                    Text(text = selectedTime, color = textColor, modifier = Modifier.padding(vertical = 8.dp))
                }

                Text(text = "Descripción", fontSize = 14.sp, color = textColor, fontWeight = FontWeight.Bold)
                Text(text = description, color = textColor, modifier = Modifier.padding(vertical = 8.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Archivos Multimedia",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(mediaFiles) { mediaFile ->
            when {
                // Mostrar imágenes
                mediaFile.filePath.endsWith(".jpg", true) || mediaFile.filePath.endsWith(".png", true) -> {
                    AsyncImage(
                        model = mediaFile.filePath,
                        contentDescription = "Imagen Adjunta",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(vertical = 8.dp)
                            .clickable {
                                selectedImageUri = mediaFile.filePath // Guardar la URI de la imagen seleccionada
                            }
                    )
                }
                // Mostrar videos con botón de maximizar
                mediaFile.filePath.endsWith(".mp4", true) -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        VideoPlayer(
                            videoUri = Uri.parse(mediaFile.filePath),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                        Button(
                            onClick = { selectedVideoUri = mediaFile.filePath },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen, // Cambia el ícono aquí
                                contentDescription = "Maximizar Video"
                            )
                        }
                    }
                }

                // Mostrar audios
                mediaFile.filePath.endsWith(".mp3", true) -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Archivo de audio",
                            style = MaterialTheme.typography.bodyLarge.copy(color = textColor)
                        )
                        Button(onClick = { player.start(File(mediaFile.filePath)) }) {
                            Text("Reproducir")
                        }
                    }
                }
                else -> {
                    Text(
                        text = "Archivo no soportado: ${mediaFile.filePath}",
                        color = textColor,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }

    // Dialog para mostrar la imagen en pantalla completa
    if (selectedImageUri != null) {
        Dialog(onDismissRequest = { selectedImageUri = null }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Imagen Agrandada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(16.dp)
                )
                IconButton(
                    onClick = { selectedImageUri = null },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = Color.White
                    )
                }
            }
        }
    }

    // Dialog para maximizar video
    if (selectedVideoUri != null) {
        Dialog(onDismissRequest = { selectedVideoUri = null }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                VideoPlayer(
                    videoUri = Uri.parse(selectedVideoUri!!),
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = { selectedVideoUri = null },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
