package com.example.wompnotes.data


import kotlinx.coroutines.flow.Flow

class NoteTaskRepository(private val noteTaskDao: NoteTaskDao) {

    // Obtener todas las notas
    suspend fun getAllNotes(): List<NoteTask> {
        return noteTaskDao.getAllNotes()
    }

    // Obtener todas las tareas
    suspend fun getAllTasks(): List<NoteTask> {
        return noteTaskDao.getAllTasks()
    }

    // Insertar una nueva nota o tarea
    suspend fun insertNoteTask(noteTask: NoteTask) {
        noteTaskDao.insert(noteTask)
    }

    // Actualizar una nota o tarea existente
    suspend fun updateNoteTask(noteTask: NoteTask) {
        noteTaskDao.update(noteTask)
    }

    // Eliminar una nota o tarea
    suspend fun deleteNoteTask(noteTask: NoteTask) {
        noteTaskDao.delete(noteTask)
    }

    // Obtener una nota o tarea por su ID
    suspend fun getNoteById(id: Int): NoteTask? {
        return noteTaskDao.getNoteById(id)
    }
}
