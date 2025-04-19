package edu.ucne.doers.presentation.padres.tareas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.ucne.doers.data.local.entity.CanjeoEntity
import edu.ucne.doers.data.local.entity.TareaHijo
import edu.ucne.doers.data.local.model.EstadoCanjeo
import edu.ucne.doers.data.local.model.EstadoTareaHijo

@Composable
fun ResumenPadreCards(
    tareasHijo: List<TareaHijo>,
    canjeos: List<CanjeoEntity>,
    goToTareasVerificar: () -> Unit,
    goToRecompensasVerificar: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Resumen de Actividades",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Tarjeta de Tareas
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { goToTareasVerificar() },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEEE9E1))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tareas por Verificar",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    val tareasPendientes = tareasHijo.filter { it.estado == EstadoTareaHijo.PENDIENTE_VERIFICACION }
                    Text(text = "${tareasPendientes.size} tareas", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Tarjeta de Recompensas
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { goToRecompensasVerificar() },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Recompensas Pendientes",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    val canjeosPendientes = canjeos.count { it.estado == EstadoCanjeo.PENDIENTE_VERIFICACION }
                    Text(text = "$canjeosPendientes recompensas", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}