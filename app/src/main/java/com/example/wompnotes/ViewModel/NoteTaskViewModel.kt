package com.example.wompnotes.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wompnotes.data.NoteTask
import com.example.wompnotes.data.NoteTaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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

    fun addOrUpdateNoteTask(noteTask: NoteTask) {
        viewModelScope.launch {
            if (noteTask.id == 0) {
                repository.insertNoteTask(noteTask)
            } else {
                repository.updateNoteTask(noteTask)
            }
            loadNotesTasks(noteTask.type == "Tarea")
        }
    }

    fun deleteNoteTask(noteTask: NoteTask) {
        viewModelScope.launch {
            repository.deleteNoteTask(noteTask)
            loadNotesTasks(noteTask.type == "Tarea")
        }
    }
}