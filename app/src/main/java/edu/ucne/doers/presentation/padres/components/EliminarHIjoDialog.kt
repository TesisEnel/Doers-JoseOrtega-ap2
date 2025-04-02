package edu.ucne.doers.presentation.padres.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import edu.ucne.doers.data.local.entity.HijoEntity

@Composable
fun EliminarHijoDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    selectedHijo: HijoEntity?
) {
    if (showDialog && selectedHijo != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                AsyncImage(
                    model = R.drawable.anxiety,
                    contentDescription = "Logo",
                    modifier = Modifier.size(100.dp).clip(CircleShape)
                )
            },
            title = { Text("Eliminando Hijo", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("¿Estás seguro de eliminar a ${selectedHijo.nombre}?", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("¡Esta acción es irreversible!", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.error)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm()
                    onDismiss()
                }) {
                    Text("Sí, eliminar", color = MaterialTheme.colorScheme.error)
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
