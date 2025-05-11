package edu.ucne.doers.presentation.componentes

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
fun PadreNavBar(
    currentScreen: Screen,
    onTareasClick: () -> Unit = {},
    onRecompensasClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {}
) {
    CustomTabBar(
        currentScreen = currentScreen,
        onTareasClick = onTareasClick,
        onRecompensasClick = onRecompensasClick,
        onPerfilClick = onPerfilClick,
        isHijo = false
    )
}