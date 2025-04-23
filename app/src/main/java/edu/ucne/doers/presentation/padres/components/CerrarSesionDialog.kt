package edu.ucne.doers.presentation.padres.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import edu.ucne.doers.R
import edu.ucne.doers.presentation.padres.PadreUiState

@Composable
fun CerrarSesionDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    padreUiState: PadreUiState
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                if (!padreUiState.fotoPerfil.isNullOrEmpty()) {
                    AsyncImage(
                        model = R.drawable.doers_logo,
                        contentDescription = "logo de la aplicación",
                        modifier = Modifier.size(100.dp).clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = padreUiState.nombre?.firstOrNull()?.toString() ?: "P", style = MaterialTheme.typography.headlineMedium)
                    }
                }
            },
            title = { Text("Cerrar Sesión", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)) },
            text = { Text("¿Estás seguro de que quieres salir?") },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm()
                    onDismiss()
                }) {
                    Text("Sí, cerrar sesión", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("No, volver atrás", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }
}
