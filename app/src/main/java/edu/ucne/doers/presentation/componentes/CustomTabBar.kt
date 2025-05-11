package edu.ucne.doers.presentation.componentes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.ucne.doers.presentation.navigation.Screen

@Composable
fun CustomTabBar(
    currentScreen: Screen,
    onTareasClick: () -> Unit,
    onRecompensasClick: () -> Unit,
    onPerfilClick: () -> Unit,
    isHijo: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFFFA000).copy(alpha = 0.2f),
                        Color(0xFF4A90E2).copy(alpha = 0.2f)
                    )
                )
            )
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TabBarItem(
            icon = Icons.Filled.Checklist,
            label = "Tareas",
            isSelected = if (isHijo) currentScreen == Screen.TareaHijo else currentScreen == Screen.TareaList,
            onClick = onTareasClick
        )

        TabBarItem(
            icon = Icons.Filled.Star,
            label = "Recompensas",
            isSelected = if (isHijo) currentScreen == Screen.RecompensaHijo else currentScreen == Screen.RecompensaList,
            onClick = onRecompensasClick
        )

        TabBarItem(
            icon = Icons.Filled.Person,
            label = "Perfil",
            isSelected = if (isHijo) currentScreen == Screen.Hijo else currentScreen == Screen.Padre,
            onClick = onPerfilClick
        )
    }
}


@Composable
fun TabBarItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (isSelected) Color(0xFFFFE0B2) else Color.Transparent, // Pastilla suave
        shadowElevation = if (isSelected) 2.dp else 0.dp,
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) Color(0xFFFFA000) else Color(0xFFE0E0E0)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color(0xFFFFA000) else Color(0xFF666666),
                modifier = Modifier.size(20.dp)
            )
            AnimatedVisibility(visible = isSelected) {
                Text(
                    text = label,
                    color = Color(0xFF4A90E2),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
