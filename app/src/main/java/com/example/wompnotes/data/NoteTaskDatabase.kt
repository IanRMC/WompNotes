package com.example.wompnotes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NoteTask::class], version = 3) // Incrementa la versión a 3
abstract class NoteTaskDatabase : RoomDatabase() {
    abstract fun noteTaskDao(): NoteTaskDao

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
                    .fallbackToDestructiveMigration() // Este método elimina y recrea la base de datos en caso de cambio de versión
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
