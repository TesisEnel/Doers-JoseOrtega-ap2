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
import edu.ucne.doers.data.local.model.PeriodicidadTarea

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodoDropdownMenu(
    selectedPeriodo: PeriodicidadTarea?,
    onPeriodoSelected: (PeriodicidadTarea) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val periocidadTarea = listOf(
        PeriodicidadTarea.DIARIA, PeriodicidadTarea.SEMANAL, PeriodicidadTarea.UNICA
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedPeriodo?.nombreMostrable ?: "Seleccione una duración",
            onValueChange = {},
            label = { Text("Duración") },
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
            periocidadTarea.forEach { estado ->
                DropdownMenuItem(
                    text = { Text(estado.nombreMostrable) },
                    onClick = {
                        onPeriodoSelected(estado)
                        expanded = false
                    }
                )
            }
        }
    }
}
