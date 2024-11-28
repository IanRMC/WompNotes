package com.example.wompnotes.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wompnotes.data.NoteTaskDao
import com.example.wompnotes.data.NoteTask
import com.example.wompnotes.data.NoteTaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class NoteTaskViewModel(private val repository: NoteTaskRepository) : ViewModel() {

    private val _notesTasks = MutableStateFlow<List<NoteTask>>(emptyList())
    val notesTasks: StateFlow<List<NoteTask>> = _notesTasks

    fun loadNotesTasks(isTaskSelected: Boolean) {
        viewModelScope.launch {
            _notesTasks.value = if (isTaskSelected) {
                repository.getAllTasks()
            } else {
                repository.getAllNotes()
            }
        }
    }

    suspend fun getNoteById(noteId: Int): NoteTask? {
        return repository.getNoteById(noteId)
    }

    suspend fun addOrUpdateNoteTask(noteTask: NoteTask): Long {
        return withContext(Dispatchers.IO) {
            val taskId: Long = if (noteTask.id == 0) {
                repository.insertNoteTask(noteTask) // Inserta y devuelve el ID generado
            } else {
                repository.updateNoteTask(noteTask) // Actualiza la tarea
                noteTask.id.toLong() // Devuelve el ID existente como Long
            }
            loadNotesTasks(noteTask.type == "Tarea") // Actualiza la lista de tareas o notas
            taskId // Devuelve el ID correcto
        }
    }

    fun deleteNoteTask(noteTask: NoteTask) {
        viewModelScope.launch {
            repository.deleteNoteTask(noteTask)
            loadNotesTasks(noteTask.type == "Tarea")
        }
    }
}
