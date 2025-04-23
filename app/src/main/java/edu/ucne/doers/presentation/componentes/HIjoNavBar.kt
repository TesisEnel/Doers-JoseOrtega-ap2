package edu.ucne.doers.presentation.recompensa.comp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import edu.ucne.doers.presentation.navigation.Screen

@Composable
fun HijoNavBar(
    currentScreen: Screen,
    onTareasClick: () -> Unit = {},
    onRecompensasClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    selectedItemColor: Color = MaterialTheme.colorScheme.primary,
    unselectedItemColor: Color = contentColor.copy(alpha = 0.6f)
) {
    NavigationBar(
        containerColor = containerColor,
        tonalElevation = 8.dp
    ) {
        // Item 1 - Tareas
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Checklist,
                    contentDescription = "Tareas",
                    tint = if (currentScreen == Screen.TareaHijo) selectedItemColor else unselectedItemColor
                )
            },
            label = {
                Text(
                    "Tareas",
                    color = if (currentScreen == Screen.TareaHijo) selectedItemColor else unselectedItemColor
                )
            },
            selected = currentScreen == Screen.TareaHijo,
            onClick = onTareasClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedItemColor,
                selectedTextColor = selectedItemColor,
                unselectedIconColor = unselectedItemColor,
                unselectedTextColor = unselectedItemColor,
                indicatorColor = containerColor
            )
        )

        // Item 2 - Recompensas
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = "Recompensas",
                    tint = if (currentScreen == Screen.RecompensaHijo) selectedItemColor else unselectedItemColor
                )
            },
            label = {
                Text(
                    "Recompensas",
                    color = if (currentScreen == Screen.RecompensaHijo) selectedItemColor else unselectedItemColor
                )
            },
            selected = currentScreen == Screen.RecompensaHijo,
            onClick = onRecompensasClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedItemColor,
                selectedTextColor = selectedItemColor,
                unselectedIconColor = unselectedItemColor,
                unselectedTextColor = unselectedItemColor,
                indicatorColor = containerColor
            )
        )

        // Item 3 - Perfil
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "Perfil",
                    tint = if (currentScreen == Screen.Hijo) selectedItemColor else unselectedItemColor
                )
            },
            label = {
                Text(
                    "Perfil",
                    color = if (currentScreen == Screen.Hijo) selectedItemColor else unselectedItemColor
                )
            },
            selected = currentScreen == Screen.Hijo,
            onClick = onPerfilClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedItemColor,
                selectedTextColor = selectedItemColor,
                unselectedIconColor = unselectedItemColor,
                unselectedTextColor = unselectedItemColor,
                indicatorColor = containerColor
            )
        )
    }
}