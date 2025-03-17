package edu.ucne.doers.presentation.tareas.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import edu.ucne.doers.data.local.model.EstadoTarea

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadoDropdownMenu(
    selectedEstado: EstadoTarea,
    onEstadoSelected: (EstadoTarea) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val estadosTarea = listOf(EstadoTarea.PENDIENTE, EstadoTarea.COMPLETADA)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedEstado.nombreMostrable,
            onValueChange = {},
            label = { Text("Estado") },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            estadosTarea.forEach { estado ->
                DropdownMenuItem(
                    text = { Text(estado.nombreMostrable) },
                    onClick = {
                        onEstadoSelected(estado)
                        expanded = false
                    }
                )
            }
        }
    }
}