package edu.ucne.doers.presentation.padres.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import edu.ucne.doers.R
import edu.ucne.doers.data.local.entity.HijoEntity

@Composable
fun AgregarPuntosDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onAgregar: (Int) -> Unit,
    selectedHijo: HijoEntity?,
    puntosAgregar: String,
    onPuntosChange: (String) -> Unit
) {
    if (showDialog && selectedHijo != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                AsyncImage(
                    model = R.drawable.celebration,
                    contentDescription = "Logo",
                    modifier = Modifier.size(100.dp).clip(CircleShape)
                )
            },
            title = {
                Column {
                    Text("Agregar Puntos", style = MaterialTheme.typography.headlineLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(selectedHijo.nombre, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyLarge)
                }
            },
            text = {
                OutlinedTextField(
                    value = puntosAgregar,
                    onValueChange = onPuntosChange,
                    label = { Text("Cantidad de Puntos") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    puntosAgregar.toIntOrNull()?.let { puntos ->
                        onAgregar(puntos)
                        onDismiss()
                    }
                }) {
                    Text("Agregar")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}
