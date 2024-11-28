package com.example.wompnotes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MediaFileDao {

    @Insert
    suspend fun insert(mediaFile: MediaFile): Long // Devuelve el ID generado automáticamente

    @Query("SELECT * FROM media_files WHERE noteId = :noteId")
    suspend fun getMediaFilesForNote(noteId: Int): List<MediaFile>

    @Delete
    suspend fun delete(mediaFile: MediaFile)

    // Método adicional para eliminar todos los archivos asociados a una nota
    @Query("DELETE FROM media_files WHERE noteId = :noteId")
    suspend fun deleteAllForNote(noteId: Int)
}