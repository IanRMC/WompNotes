package com.example.wompnotes.ui.screens

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.net.Uri
import android.widget.CalendarView
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.wompnotes.data.NoteTask
import com.example.wompnotes.ViewModel.NoteTaskViewModel
import com.example.wompnotes.R
import java.util.Calendar
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.avance.notifications.NotificationReceiver
import java.text.SimpleDateFormat
import java.util.Locale
import android.util.Log
import android.os.SystemClock
import androidx.core.content.ContextCompat

@SuppressLint("ScheduleExactAlarm")
fun scheduleNotification(context: Context, noteTask: NoteTask) {
    if (noteTask.date != null) {
        try {
            // Intent para activar el BroadcastReceiver
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra("title", noteTask.title)
                putExtra("description", noteTask.description)
            }


            val pendingIntent = PendingIntent.getBroadcast(
                context,
                noteTask.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Convertir la fecha de la tarea a tiempo en milisegundos
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.time = format.parse(noteTask.date) ?: throw IllegalArgumentException("Fecha no válida")

            Log.d("scheduleNotification", "Fecha de la tarea convertida correctamente: ${calendar.time}")

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

            val triggerTime = SystemClock.elapsedRealtime() + calendar.timeInMillis - System.currentTimeMillis()
            alarmManager.setExact(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                pendingIntent
            )


            Log.d("scheduleNotification", "Notificación programada exitosamente para ${calendar.time}")

        } catch (e: Exception) {
            Log.e("scheduleNotification", "Error al programar la notificación: ${e.message}")
            e.printStackTrace()
            Toast.makeText(context, "Error al programar la notificación", Toast.LENGTH_SHORT).show()
        }
    } else {
        Log.e("scheduleNotification", "La fecha de la tarea es nula.")
        Toast.makeText(context, "La fecha de la tarea es nula.", Toast.LENGTH_SHORT).show()
    }
}


@Composable
fun PantallaAgregar(
    navController: NavController,
    noteId: Int,
    initialType: String,  // Tipo inicial que viene de la navegación
    viewModel: NoteTaskViewModel = viewModel()
) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val borderColor = if (isDarkTheme) Color.Gray else Color.LightGray

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(initialType) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isCalendarVisible by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("Seleccione una fecha") }
    var selectedTime by remember { mutableStateOf("Seleccione una hora") }

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { /* Lógica para manejar el archivo seleccionado */ } }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap -> bitmap?.let { /* Lógica para manejar la imagen capturada */ } }

    LaunchedEffect(noteId) {
        if (noteId != -1) {
            val note = viewModel.notesTasks.value.find { it.id == noteId }
            note?.let {
                title = it.title
                description = it.description
                selectedType = it.type
                val parts = it.date?.split(" ") ?: listOf("Seleccione una fecha", "Seleccione una hora")
                selectedDate = parts.getOrElse(0) { "Seleccione una fecha" }
                selectedTime = parts.getOrElse(1) { "Seleccione una hora" }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { navController.popBackStack() }) {
                Text(stringResource(R.string.cancelar), color = textColor)
            }
            Text(

                text = if (noteId == -1) stringResource(R.string.nueva_nota) else stringResource(R.string.editar_nota),
                style = MaterialTheme.typography.titleLarge.copy(color = textColor)
            )
            TextButton(onClick = {
                if (title.isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.el_t_tulo_es_obligatorio), Toast.LENGTH_SHORT).show()
                } else if (selectedType == "Tarea" && (selectedDate == "Seleccione una fecha" || selectedTime == "Seleccione una hora")) {
                    Toast.makeText(context, context.getString(R.string.la_fecha_y_la_hora_son_obligatorias_para_tareas), Toast.LENGTH_SHORT).show()
                } else {
                    val dateTime = if (selectedType == "Tarea") "$selectedDate $selectedTime" else null
                    val newNoteTask = NoteTask(
                        id = if (noteId == -1) 0 else noteId,
                        title = title,
                        description = description,
                        date = dateTime,
                        type = selectedType
                    )
                    viewModel.addOrUpdateNoteTask(newNoteTask)
                    scheduleNotification(context, newNoteTask)  // Programar la notificación
                    navController.popBackStack()
                }
            }) {
                Text(if (noteId == -1) stringResource(R.string.agregar) else stringResource(R.string.guardar), color = textColor)
            }

        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.t_tulo), fontSize = 14.sp, color = textColor)
                BasicTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(if (isDarkTheme) Color.DarkGray else Color(0xFFE8EAF6)),
                    textStyle = TextStyle(fontSize = 18.sp, color = textColor)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.descripci_n), fontSize = 14.sp, color = textColor)
                BasicTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(if (isDarkTheme) Color.DarkGray else Color(0xFFE8EAF6)),
                    textStyle = TextStyle(fontSize = 16.sp, color = textColor)
                )
            }
        }

        // Ocultar el selector de tipo cuando ya se ha definido
        // Dentro del bloque de la UI de selección de tipo
        if (noteId == -1 && initialType.isEmpty()) {
            Box {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isDropdownExpanded = true }
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE5E3E9))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Tipo", tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = selectedType.ifEmpty { stringResource(R.string.seleccionar_tipo) }, color = Color.Black) // Mostrar "Seleccionar tipo" si está vacío
                        }
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Abrir menú", tint = Color.Black)
                    }
                }

                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.tarea), color = if (isDarkTheme) Color.White else Color.Black) },
                        onClick = {
                            selectedType = "Tarea"  // Actualiza el texto a "Tarea"
                            isDropdownExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.nota), color = if (isDarkTheme) Color.White else Color.Black) },
                        onClick = {
                            selectedType = "Nota"  // Actualiza el texto a "Nota"
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectedType == "Tarea") {
                IconButton(onClick = { isCalendarVisible = !isCalendarVisible }) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Calendario",
                        tint = textColor
                    )
                }
            }
        }

        if (isCalendarVisible) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.fecha_y_hora),
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AndroidView(
                        factory = { context ->
                            CalendarView(context).apply {
                                // Aplicar colores personalizado
                                
                                if (isDarkTheme) setDateTextAppearance(R.style.CalendarTextAppearanceDark) else setDateTextAppearance(R.style.CalendarTextAppearance)
                                setOnDateChangeListener { _, year, month, dayOfMonth ->
                                    selectedDate = "$dayOfMonth/${month + 1}/$year"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                val calendar = Calendar.getInstance()
                                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                                val minute = calendar.get(Calendar.MINUTE)

                                TimePickerDialog(context, { _, selectedHour, selectedMinute ->
                                    selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                                }, hour, minute, true).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE))
                        ) {
                            Icon(imageVector = Icons.Default.Schedule, contentDescription = "Hora", tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.hora), color = Color.Black)
                        }

                        Button(
                            onClick = { isCalendarVisible = false },
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE))
                        ) {
                            Icon(imageVector = Icons.Default.DateRange, contentDescription = "Recordar", tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.recordar), color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}
