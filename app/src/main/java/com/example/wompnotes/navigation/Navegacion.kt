package com.example.wompnotes.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wompnotes.R
import com.example.wompnotes.ViewModel.NoteTaskViewModel
import com.example.wompnotes.ViewModel.ViewModelFactory
import com.example.wompnotes.data.NoteTaskRepository
import com.example.wompnotes.ui.screens.PantallaAgregar
import com.example.wompnotes.ui.screens.Principal
import com.example.wompnotes.ui.screens.VerPantalla
import com.example.wompnotes.ui.screens.ListaNotaTarea

@Composable
fun MyApp(noteTaskRepository: NoteTaskRepository) {
    val darkTheme = isSystemInDarkTheme()
    val colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()
    val viewModel: NoteTaskViewModel = viewModel(factory = ViewModelFactory(noteTaskRepository))
    val navController = rememberNavController()

    MaterialTheme(colorScheme = colorScheme) {
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            composable("home") {
                Principal(navController)
            }

            composable(
                "secondScreen?noteId={noteId}&type={type}",
                arguments = listOf(
                    navArgument("noteId") { type = NavType.IntType; defaultValue = -1 },
                    navArgument("type") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
                val type = backStackEntry.arguments?.getString("type") ?: ""
                PantallaAgregar(
                    navController = navController,
                    noteId = noteId,
                    initialType = type,
                    viewModel = viewModel
                )
            }

            // Ruta para abrir la pantalla de visualización de una nota o tarea
            composable(
                "verPantalla?noteId={noteId}",
                arguments = listOf(navArgument("noteId") { type = NavType.IntType })
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
                VerPantalla(navController = navController, noteId = noteId, viewModel = viewModel)
            }

            composable(
                "notesTasksScreen/{item}",
                arguments = listOf(navArgument("item") { type = NavType.StringType })
            ) { backStackEntry ->
                val item = backStackEntry.arguments?.getString("item") ?: "Notas/Tareas"
                val isTaskSelected = item == "Tareas"
                ListaNotaTarea(
                    title = if (isTaskSelected) stringResource(R.string.tareas) else stringResource(
                        R.string.notas
                    ),
                    navController = navController,
                    viewModel = viewModel,
                    isTaskSelected = isTaskSelected
                )
            }
        }
    }
}