package com.example.wompnotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wompnotes.ViewModel.NoteTaskViewModel
import com.example.wompnotes.R
import kotlinx.coroutines.launch

@Composable
fun ListaNotaTarea(
    title: String,
    navController: NavController,
    viewModel: NoteTaskViewModel,
    isTaskSelected: Boolean
) {
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val backgroundColor = if (isDarkTheme) Color.DarkGray else Color(0xFFE8EAF6)
    val borderColor = if (isDarkTheme) Color.LightGray else Color.Black
    val notes by viewModel.notesTasks.collectAsState()

    LaunchedEffect(isTaskSelected) {
        viewModel.loadNotesTasks(isTaskSelected)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(color = textColor),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        notes.forEach { note ->
            TaskItem(
                title = note.title,
                description = note.description,
                date = note.date,
                textColor = textColor,
                backgroundColor = backgroundColor,
                borderColor = borderColor,
                isTask = note.type == "Tarea",
                onEdit = {
                    navController.navigate("secondScreen?noteId=${note.id}&type=${note.type}")
                },
                onClick = {
                    navController.navigate("verPantalla?noteId=${note.id}")
                },
                onDelete = {
                    viewModel.deleteNoteTask(note)
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))



        Spacer(modifier = Modifier.height(12.dp))

        StyledAddButton(
            onClick = { navController.navigate("secondScreen?type=${if (isTaskSelected) "Tarea" else "Nota"}") },
            backgroundColor = backgroundColor,
            textColor = textColor
        )
    }
}

@Composable
fun StyledAddButton(onClick: () -> Unit, backgroundColor: Color, textColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar nueva", tint = textColor)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.agregar_nueva), color = textColor)
        }
    }
}

@Composable
fun TaskItem(
    title: String,
    description: String,
    date: String?,
    textColor: Color,
    backgroundColor: Color,
    borderColor: Color,
    isTask: Boolean,
    onEdit: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }  // Estado del checkbox
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (isTask) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { checked ->
                    isChecked = checked  // Actualiza el estado del checkbox
                    if (checked) {
                        // Iniciar un retraso de 3 segundos para eliminar la tarea
                        coroutineScope.launch {
                            kotlinx.coroutines.delay(3000)
                            if (isChecked) {  // Verificar si aún está marcado
                                onDelete()
                                isChecked = false  // Restablece el estado del checkbox
                            }
                        }
                    }
                }
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge.copy(color = textColor))
            Text(text = description, style = MaterialTheme.typography.bodySmall.copy(color = textColor))
            date?.let {
                Text(text = it, style = MaterialTheme.typography.bodySmall.copy(color = textColor))
            }
        }

        IconButton(onClick = onEdit) {
            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = textColor)
        }
        IconButton(onClick = { showDeleteConfirmation = true }) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = textColor)
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(text = stringResource(R.string.confirmaci_n_de_eliminaci_n)) },
            text = { Text(text = stringResource(R.string.est_s_seguro_de_que_deseas_eliminar)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirmation = false
                    onDelete()
                    isChecked = false  // Restablece el estado del checkbox tras la eliminación
                }) {
                    Text(stringResource(R.string.eliminar), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(stringResource(R.string.cancelar))
                }
            }
        )
    }
}
