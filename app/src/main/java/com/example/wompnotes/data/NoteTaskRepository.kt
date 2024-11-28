package com.example.wompnotes.data


import kotlinx.coroutines.flow.Flow

class NoteTaskRepository(private val noteTaskDao: NoteTaskDao) {

    // Inserta una nueva nota o tarea y devuelve el ID generado
    suspend fun insertNoteTask(noteTask: NoteTask): Long {
        return noteTaskDao.insertNoteTask(noteTask) // Devuelve el ID generado
    }

    // Actualiza una nota o tarea existente
    suspend fun updateNoteTask(noteTask: NoteTask) {
        noteTaskDao.update(noteTask) // No devuelve nada
    }

    // Obtiene todas las notas
    suspend fun getAllNotes(): List<NoteTask> {
        return noteTaskDao.getAllNotes()
    }

    // Obtiene todas las tareas
    suspend fun getAllTasks(): List<NoteTask> {
        return noteTaskDao.getAllTasks()
    }

    // Elimina una nota o tarea
    suspend fun deleteNoteTask(noteTask: NoteTask) {
        noteTaskDao.delete(noteTask)
    }

    // Obtiene una nota o tarea por su ID
    suspend fun getNoteById(id: Int): NoteTask? {
        return noteTaskDao.getNoteById(id)
    }
}