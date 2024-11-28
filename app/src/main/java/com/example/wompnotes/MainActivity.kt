package com.example.wompnotes

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.wompnotes.ui.theme.WompNotesTheme
import com.example.wompnotes.data.NoteTaskDatabase
import com.example.wompnotes.navigation.MyApp
import android.Manifest
import android.app.AlarmManager
import android.content.pm.PackageManager
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.provider.Settings
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.wompnotes.ViewModel.NoteTaskViewModel
import com.example.wompnotes.ViewModel.ViewModelFactory
import com.example.wompnotes.data.NoteTaskRepository
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = NoteTaskDatabase.getDatabase(applicationContext)
        val repository = NoteTaskRepository(database.noteTaskDao())
        val viewModelFactory = ViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[NoteTaskViewModel::class.java] // Instancia del ViewModel



        // Solicitar permiso para notificaciones en Android 13 y superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                AlertDialog.Builder(this)
                    .setTitle("Permiso de Alarma Exacta")
                    .setMessage("Para programar notificaciones exactas, habilita el permiso de alarma exacta en los ajustes de la aplicación.")
                    .setPositiveButton("Ir a ajustes") { _, _ ->
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        startActivity(intent)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }

        // Solicitar permisos necesarios
        requestPermissions()

        setContent {
            WompNotesTheme {
                MyApp(repository)
            }
        }

    }

    private fun requestPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                101
            )
        }
    }

}
