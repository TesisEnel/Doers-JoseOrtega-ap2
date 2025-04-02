package edu.ucne.doers.presentation.componentes

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
fun PadreNavBar(
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
            icon = { Icon(Icons.Filled.Checklist, contentDescription = "Tarea", tint = Color.White) },
            label = { Text("Tarea", color = Color.White) },
            selected = currentScreen == Screen.TareaList,
            onClick = onTareasClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Star, contentDescription = "Recompensas", tint = Color.White) },
            label = { Text("Recompensas", color = Color.White) },
            selected = currentScreen == Screen.RecompensaList,
            onClick = onRecompensasClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil", tint = Color.White) },
            label = { Text("Perfil", color = Color.White) },
            selected = currentScreen == Screen.Padre,
            onClick = onPerfilClick
        )
    }
}