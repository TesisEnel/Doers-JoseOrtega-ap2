package edu.ucne.doers.presentation.tareas.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.ucne.doers.data.local.entity.TareaEntity
import edu.ucne.doers.data.local.model.EstadoTarea
import edu.ucne.doers.presentation.tareas.padre.TareaViewModel

@Composable
fun TaskOverview(
    modifier: Modifier = Modifier,
    tareas: List<TareaEntity>,
    onEdit: (Int) -> Unit,
    onDelete: (TareaEntity) -> Unit,
    viewModel: TareaViewModel = hiltViewModel()
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isHorizontal = maxWidth > maxHeight

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (!isHorizontal) {
                Text(
                    text = "Reporte de Tareas",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(bottom = 16.dp, top = 22.dp)
                )

                AnimatedVisibility(visible = !isHorizontal) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TaskCounter(
                            "Tareas completadas",
                            tareas.count { it.estado == EstadoTarea.COMPLETADA },
                            EstadoTarea.COMPLETADA
                        )
                        TaskCounter(
                            "Tareas pendientes",
                            tareas.count { it.estado == EstadoTarea.PENDIENTE },
                            EstadoTarea.PENDIENTE
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = if (tareas.isEmpty()) "No se han creado tareas" else "Listado de pendientes",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(tareas) { tarea ->
                    TaskCard(
                        tarea,
                        onEdit = onEdit,
                        onDelete = onDelete,
                        onCondicionChange = { nuevaCondicion ->
                            viewModel.onCondicionChange(tarea.tareaId, nuevaCondicion)
                        }
                    )
                }
            }
        }
    }
}

