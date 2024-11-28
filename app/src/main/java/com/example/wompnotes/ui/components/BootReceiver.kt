package com.example.wompnotes.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.wompnotes.data.NoteTaskDatabase
import com.example.wompnotes.data.NoteTaskRepository
import com.example.wompnotes.ui.screens.scheduleNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.content.edit

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Accede a la base de datos para reprogramar las notificaciones
            val database = NoteTaskDatabase.getDatabase(context)
            val repository = NoteTaskRepository(database.noteTaskDao())

            CoroutineScope(Dispatchers.IO).launch {
                val tasks = repository.getAllTasks() // Obtiene todas las tareas con notificaciones pendientes
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                tasks.forEachIndexed { index, task ->
                    val dateTimeParts = task.date?.split(";") ?: emptyList()
                    dateTimeParts.forEachIndexed { subIndex, dateTime ->
                        val triggerTime = sdf.parse(dateTime)?.time ?: return@forEachIndexed
                        if (triggerTime > System.currentTimeMillis()) {
                            scheduleNotification(
                                context = context,
                                noteTask = task.copy(date = dateTime),
                                uniqueId = task.id * 1000 + subIndex
                            )
                        }
                    }
                }
            }
        }
    }
}
