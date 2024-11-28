package com.example.wompnotes.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wompnotes.data.NoteTaskRepository

class ViewModelFactory(private val repository: NoteTaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteTaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteTaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
