package com.example.wompnotes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteTaskDao {
    @Insert
    suspend fun insert(noteTask: NoteTask)

    @Query("SELECT * FROM notes_tasks WHERE id = :id")
    suspend fun getNoteById(id: Int): NoteTask?

    @Update
    suspend fun update(noteTask: NoteTask)

    @Query("SELECT * FROM notes_tasks WHERE type = 'Nota'")
    suspend fun getAllNotes(): List<NoteTask>

    @Query("SELECT * FROM notes_tasks WHERE type = 'Tarea'")
    suspend fun getAllTasks(): List<NoteTask>

    @Delete
    suspend fun delete(noteTask: NoteTask)
}
