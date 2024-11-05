package com.example.wompnotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.wompnotes.ViewModel.NoteTaskViewModel

@Composable
fun VerPantalla(
    navController: NavController,
    noteId: Int,
    viewModel: NoteTaskViewModel = viewModel()
) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val borderColor = if (isDarkTheme) Color.Gray else Color.LightGray

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }

    // Cargar los datos de la nota o tarea según el ID
    LaunchedEffect(noteId) {
        val note = viewModel.notesTasks.value.find { it.id == noteId }
        note?.let {
            title = it.title
            description = it.description
            selectedTime = it.date ?: "Fecha no especificada"
            type = it.type
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Atrás", color = textColor)
            }
            Text(
                text = title,  // Usamos el título específico de la nota o tarea
                style = MaterialTheme.typography.titleLarge.copy(color = textColor)
            )
            Spacer(modifier = Modifier.width(48.dp))  // Espacio para balancear la UI
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Título", fontSize = 14.sp, color = textColor, fontWeight = FontWeight.Bold)
                BasicTextField(
                    value = title,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(if (isDarkTheme) Color.DarkGray else Color(0xFFE8EAF6)),
                    textStyle = TextStyle(fontSize = 18.sp, color = textColor)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mostrar la "Hora" solo si es una Tarea
                if (type == "Tarea") {
                    Text(text = "Hora", fontSize = 14.sp, color = textColor, fontWeight = FontWeight.Bold)
                    BasicTextField(
                        value = selectedTime,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(if (isDarkTheme) Color.DarkGray else Color(0xFFE8EAF6)),
                        textStyle = TextStyle(fontSize = 16.sp, color = textColor)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(text = "Descripción", fontSize = 14.sp, color = textColor, fontWeight = FontWeight.Bold)
                BasicTextField(
                    value = description,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(if (isDarkTheme) Color.DarkGray else Color(0xFFE8EAF6)),
                    textStyle = TextStyle(fontSize = 16.sp, color = textColor)
                )
            }
        }
    }
}
