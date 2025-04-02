package edu.ucne.doers.presentation.recompensa.comp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import edu.ucne.doers.presentation.navigation.Screen

@Composable
fun HijoNavBar(
    currentScreen: Screen,
    onTareasClick: () -> Unit = {},
    onRecompensasClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {}
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Checklist, contentDescription = "Tareas", tint = Color.White) },
            label = { Text("Tareas", color = Color.White) },
            selected = currentScreen == Screen.TareaHijo,
            onClick = onTareasClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Star, contentDescription = "Recompensas", tint = Color.White) },
            label = { Text("Recompensas", color = Color.White) },
            selected = currentScreen == Screen.RecompensaHijo,
            onClick = onRecompensasClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil", tint = Color.White) },
            label = { Text("Perfil", color = Color.White) },
            selected = currentScreen == Screen.Hijo,
            onClick = onPerfilClick
        )
    }
}