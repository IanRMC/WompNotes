package com.example.wompnotes.ui.screens

import com.example.wompnotes.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.res.stringResource

// Pantalla principal
// Pantalla principal
@Composable
fun Principal(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())  // Habilitar el scroll vertical
    ) {

        // Lista de notas y tareas
        Text(
            stringResource(R.string.mis_listas),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Cambiar para que navegue a la tercera pantalla
        SimpleListCard(
            title = stringResource(R.string.notas),
            icon = Icons.Default.Menu,
            onClick = { navController.navigate("notesTasksScreen/Notas") }
        )

        SimpleListCard(
            title = stringResource(R.string.tareas),
            icon = Icons.Default.Menu,
            onClick = { navController.navigate("notesTasksScreen/Tareas") }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Botón Agregar nueva
        SimpleListCard(
            title = stringResource(R.string.agregar_nueva),
            icon = Icons.Default.Add,
            onClick = { navController.navigate("secondScreen") }  // Navegar sin especificar tipo
        )
    }
}


// Función para las tarjetas simples que se comportan como botones
@Composable
fun SimpleListCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = title)
                Spacer(modifier = Modifier.width(8.dp))
                Text(title)
            }
        }
    }
}

// Función original para las listas con ítems
@Composable
fun ListCard(
    title: String,
    icon: ImageVector,
    items: List<String>,
    onItemClick: ((String) -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = icon, contentDescription = title)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(title)
                }
                if (items.isNotEmpty()) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Desplegar")
                }
            }
            if (expanded && items.isNotEmpty()) {
                Column {
                    items.forEach { item ->
                        Text(
                            text = item,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable { onItemClick?.invoke(item) }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun StatusCard(title: String, count: Int, modifier: Modifier = Modifier, icon: ImageVector) {
    Card(
        modifier = modifier.height(120.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(32.dp)
                )
                Text(text = "$count", style = MaterialTheme.typography.headlineMedium)
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )
        }
    }
}