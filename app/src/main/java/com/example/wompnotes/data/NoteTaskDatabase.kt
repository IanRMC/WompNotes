package com.example.wompnotes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NoteTask::class, MediaFile::class], version = 5) // Incrementa la versión
abstract class NoteTaskDatabase : RoomDatabase() {
    abstract fun noteTaskDao(): NoteTaskDao
    abstract fun mediaFileDao(): MediaFileDao

    companion object {
        @Volatile
        private var INSTANCE: NoteTaskDatabase? = null

        fun getDatabase(context: Context): NoteTaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteTaskDatabase::class.java,
                    "note_task_database"
                )
                    .fallbackToDestructiveMigration() // Destruye y recrea la base de datos en caso de cambios
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}