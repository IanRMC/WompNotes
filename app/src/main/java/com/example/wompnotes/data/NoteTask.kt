package com.example.wompnotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_tasks")
data class NoteTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val date: String? = null,
    val type: String  // Valor predeterminado como "Nota" para evitar nulos
)
