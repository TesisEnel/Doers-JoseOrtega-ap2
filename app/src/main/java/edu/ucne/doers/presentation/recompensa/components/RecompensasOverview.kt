package edu.ucne.doers.presentation.recompensa.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.ucne.doers.data.local.entity.RecompensaEntity
import edu.ucne.doers.data.local.model.CondicionRecompensa
import edu.ucne.doers.data.local.model.EstadoRecompensa
import edu.ucne.doers.presentation.recompensa.RecompensaUiState
import edu.ucne.doers.presentation.recompensa.RecompensaViewModel
import edu.ucne.doers.presentation.tareas.components.TaskCounterRecompensa

@Composable
fun RecompensasOverview(
    modifier: Modifier = Modifier,
    recompensas: List<RecompensaUiState>,
    onEdit: (Int) -> Unit,
    onDelete: (RecompensaEntity) -> Unit,
    onCondicionChange: (Int, CondicionRecompensa) -> Unit,
    viewModel: RecompensaViewModel = hiltViewModel(),
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
                    text = "Reporte de Recompensas",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp, top = 22.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TaskCounterRecompensa(
                        label = "Recompensas disponibles",
                        count = recompensas.count { it.estado == EstadoRecompensa.DISPONIBLE },
                        estado = EstadoRecompensa.DISPONIBLE
                    )
                    TaskCounterRecompensa(
                        label = "Recompensas pendientes",
                        count = recompensas.count { it.estado == EstadoRecompensa.NO_DISPONIBLE },
                        estado = EstadoRecompensa.PENDIENTE
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = if (recompensas.isEmpty()) "No se han creado recompensas" else "Listado de recompensas",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(recompensas) { recompensa ->
                    RecompensaCard(
                        recompensa = recompensa,
                        onEdit = onEdit,
                        onDelete = onDelete,
                        onCondicionChange = onCondicionChange
                    )
                }
            }
        }
    }
}