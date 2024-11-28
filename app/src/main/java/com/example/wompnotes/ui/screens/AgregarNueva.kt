package com.example.wompnotes.ui.screens

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.avance.notifications.NotificationReceiver
import com.example.wompnotes.ComposeFileProvider
import com.example.wompnotes.ViewModel.NoteTaskViewModel
import com.example.wompnotes.audio.AndroidAudioPlayer
import com.example.wompnotes.audio.AndroidAudioRecorder
import com.example.wompnotes.data.MediaFile
import com.example.wompnotes.data.NoteTask
import com.example.wompnotes.data.NoteTaskDatabase
import com.example.wompnotes.ui.components.VideoPlayerWithDelete
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarNotaTarea(
    navController: NavController,
    noteId: Int,
    initialType: String,
    viewModel: NoteTaskViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val mediaFileDao = NoteTaskDatabase.getDatabase(context).mediaFileDao()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(initialType) }
    val selectedDates = remember { mutableStateListOf<Pair<String, String>>() }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var audioFile: File? by remember { mutableStateOf(null) }

    val temporaryMediaFiles = remember { mutableStateListOf<MediaFile>() }
    val recorder = remember { AndroidAudioRecorder(context) }
    val player = remember { AndroidAudioPlayer(context) }

    LaunchedEffect(noteId) {
        if (noteId != -1) {
            val note = viewModel.notesTasks.value.find { it.id == noteId }
            note?.let {
                title = it.title
                description = it.description
                selectedType = it.type
                it.date?.split(";")?.forEach { dateTime ->
                    val parts = dateTime.split(" ")
                    if (parts.size == 2) {
                        selectedDates.add(Pair(parts[0], parts[1]))
                    }
                }
                temporaryMediaFiles.addAll(mediaFileDao.getMediaFilesForNote(noteId))
            }
        }
    }

    LaunchedEffect(Unit) {
        photoUri = ComposeFileProvider.getImageUri(context)
    }

    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            photoUri?.let { uri ->
                temporaryMediaFiles.add(MediaFile(noteId = noteId, filePath = uri.toString()))
            }
        } else {
            Log.e("Camera", "Captura de foto fallida")
        }
    }

    val videoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
        if (success) {
            videoUri?.let { uri ->
                temporaryMediaFiles.add(MediaFile(noteId = noteId, filePath = uri.toString()))
            }
        } else {
            Log.e("Video", "Captura de video fallida")
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { navController.popBackStack() }) { Text("Cancelar") }
            Text(
                text = if (noteId == -1) "Nueva ${if (selectedType == "Tarea") "Tarea" else "Nota"}" else "Editar ${selectedType}",
                style = MaterialTheme.typography.titleLarge
            )
            TextButton(onClick = {
                if (title.isEmpty()) {
                    Toast.makeText(context, "El título es obligatorio", Toast.LENGTH_SHORT).show()
                } else if (selectedType == "Tarea" && selectedDates.isEmpty()) {
                    Toast.makeText(context, "Debe agregar al menos una fecha y hora para la tarea", Toast.LENGTH_SHORT).show()
                } else {
                    val dateTime = if (selectedType == "Tarea") {
                        selectedDates.joinToString(";") { "${it.first} ${it.second}" }
                    } else null
                    val newNoteTask = NoteTask(
                        id = if (noteId == -1) 0 else noteId,
                        title = title,
                        description = description,
                        date = dateTime,
                        type = selectedType
                    )
                    scope.launch {
                        // Si es edición, cancelar notificaciones antiguas
                        if (noteId != -1) {
                            val oldNoteTask = viewModel.getNoteById(noteId)
                            oldNoteTask?.date?.split(";")?.forEachIndexed { index, oldDate ->
                                val uniqueId = noteId * 1000 + index
                                cancelNotification(context, uniqueId)
                            }
                        }

                        // Guardar o actualizar la tarea
                        val taskId = viewModel.addOrUpdateNoteTask(newNoteTask)

                        // Programar las nuevas notificaciones
                        selectedDates.forEachIndexed { index, pair ->
                            val dateTime = "${pair.first} ${pair.second}"
                            scheduleNotification(
                                context = context,
                                noteTask = newNoteTask.copy(date = dateTime),
                                uniqueId = taskId.toInt() * 1000 + index // ID único para cada notificación
                            )
                        }

                        // Verificar y guardar archivos multimedia
                        val existingMediaFiles = mediaFileDao.getMediaFilesForNote(taskId.toInt())
                        temporaryMediaFiles.forEach { media ->
                            if (!existingMediaFiles.any { it.filePath == media.filePath }) {
                                mediaFileDao.insert(media.copy(noteId = taskId.toInt()))
                            }
                        }

                        navController.popBackStack()
                    }
                }
            }) { Text("Guardar") }
        }

        TextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            label = { Text("Título") }
        )
        TextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            label = { Text("Descripción") }
        )

        if (selectedType == "Tarea") {
            Text("Fechas y Horas de Notificación:")
            Spacer(modifier = Modifier.height(8.dp))
            selectedDates.forEachIndexed { index, pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${pair.first} ${pair.second}")
                    IconButton(onClick = { selectedDates.removeAt(index) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar Fecha")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                showDatePickerDialog(context) { year, month, day ->
                    val selectedDate = "$day/${month + 1}/$year"
                    showTimePickerDialog(context) { hour, minute ->
                        val selectedTime = String.format("%02d:%02d", hour, minute)
                        selectedDates.add(Pair(selectedDate, selectedTime))
                    }
                }
            }) { Text("Agregar Fecha y Hora") }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Archivos Adjuntos")
        LazyColumn(modifier = Modifier.height(150.dp)) {
            items(temporaryMediaFiles) { mediaFile ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    when {
                        mediaFile.filePath.endsWith(".jpg", true) -> {
                            AsyncImage(
                                model = mediaFile.filePath,
                                contentDescription = "Imagen Adjunta",
                                modifier = Modifier.size(100.dp)
                            )
                        }

                        mediaFile.filePath.endsWith(".mp4", true) -> {
                            if (mediaFile.filePath.endsWith(".mp4", true)) {
                                VideoPlayerWithDelete(
                                    videoUri = Uri.parse(mediaFile.filePath),
                                    noteId = noteId,
                                    mediaFile = mediaFile,
                                    onDeleteFromList = { temporaryMediaFiles.remove(mediaFile) }
                                )
                            }
                        }

                        mediaFile.filePath.endsWith(".mp3", true) -> {
                            Button(onClick = {
                                player.start(File(mediaFile.filePath))
                            }) {
                                Text("Reproducir Audio")
                            }
                        }
                    }
                    IconButton(
                        onClick = {
                            temporaryMediaFiles.remove(mediaFile) // Remover de la lista temporal

                            // Si la nota/tarea ya existe en la base de datos (modo edición)
                            if (noteId != -1) {
                                scope.launch {
                                    val mediaFileDao = NoteTaskDatabase.getDatabase(context).mediaFileDao()
                                    mediaFileDao.delete(mediaFile) // Eliminar el archivo de la base de datos
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar Archivo",
                            tint = Color.Red
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { photoUri?.let { photoLauncher.launch(it) } }) { Text("Tomar Foto") }
            Button(onClick = {
                if (!isRecording) {
                    val file = File(context.cacheDir, "audio_${System.currentTimeMillis()}.mp3")
                    recorder.start(file)
                    audioFile = file
                    isRecording = true
                } else {
                    recorder.stop()
                    isRecording = false
                    audioFile?.let { file ->
                        temporaryMediaFiles.add(MediaFile(noteId = noteId, filePath = file.absolutePath))
                    }
                }
            }) { Text(if (isRecording) "Detener Audio" else "Grabar Audio") }
            Button(onClick = {
                videoUri = ComposeFileProvider.getVideoUri(context)
                videoUri?.let { videoLauncher.launch(it) }
            }) {
                Text("Tomar Video")
            }
        }
    }
}


// Función para programar la notificación
@SuppressLint("ScheduleExactAlarm")
fun scheduleNotification(context: Context, noteTask: NoteTask, uniqueId: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("title", noteTask.title)
        putExtra("description", noteTask.description)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        uniqueId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val triggerTime = sdf.parse(noteTask.date)?.time ?: System.currentTimeMillis()

    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        triggerTime,
        pendingIntent
    )
}


// Funciones auxiliares
fun showDatePickerDialog(context: Context, onDateSelected: (Int, Int, Int) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
        onDateSelected(selectedYear, selectedMonth, selectedDay)
    }, year, month, day).show()
}

fun showTimePickerDialog(context: Context, onTimeSelected: (Int, Int) -> Unit) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    TimePickerDialog(context, { _, selectedHour, selectedMinute ->
        onTimeSelected(selectedHour, selectedMinute)
    }, hour, minute, true).show()
}

fun cancelNotification(context: Context, uniqueId: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, NotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        uniqueId,
        intent,
        PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
    )
    pendingIntent?.let {
        alarmManager.cancel(it)
        it.cancel()
    }
}

