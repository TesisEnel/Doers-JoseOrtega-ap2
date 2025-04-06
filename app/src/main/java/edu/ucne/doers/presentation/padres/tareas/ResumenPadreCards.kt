package edu.ucne.doers.presentation.padres.tareas


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.ucne.doers.data.local.entity.HijoEntity
import edu.ucne.doers.data.local.entity.RecompensaEntity
import edu.ucne.doers.data.local.entity.TareaEntity
import edu.ucne.doers.data.local.entity.TareaHijo
import edu.ucne.doers.data.local.model.EstadoRecompensa
import edu.ucne.doers.data.local.model.EstadoTareaHijo

@Composable
fun ResumenPadreCards(
    hijos: List<HijoEntity>,
    tareasHijo: List<TareaHijo>,
    recompensasPendientesMap: Map<String, List<RecompensaEntity>>,
    tareas: List<TareaEntity>,
    onCardClick: (String) -> Unit,
    onValidarTarea: (TareaHijo) -> Unit,
    onNoValidarTarea: (TareaHijo) -> Unit
) {
    var expandedCard by remember { mutableStateOf<String?>(null) }

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
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        expandedCard = if (expandedCard == "tareas") null else "tareas"
                        onCardClick("tareas")
                    },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Tareas por Verificar",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (expandedCard == "tareas") Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null
                        )
                    }
                    val tareasPendientes = tareasHijo.filter { it.estado == EstadoTareaHijo.PENDIENTE_VERIFICACION }
                    Text(text = "${tareasPendientes.size} tareas", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        expandedCard = if (expandedCard == "recompensas") null else "recompensas"
                        onCardClick("recompensas")
                    },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Recompensas Pendientes",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (expandedCard == "recompensas") Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null
                        )
                    }
                    val totalRecompensas = recompensasPendientesMap.values.flatten()
                        .count { it.estado == EstadoRecompensa.PENDIENTE.toString() }
                    Text(text = "$totalRecompensas recompensas", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (expandedCard == "tareas") {
            tareasHijo.filter { it.estado == EstadoTareaHijo.PENDIENTE_VERIFICACION }.forEach { tarea ->
                val hijo = hijos.find { it.hijoId == tarea.hijoId }
                val tareaDesc = tareas.find { it.tareaId == tarea.tareaId }?.descripcion ?: "Tarea desconocida"
                val nombreHijo = hijo?.nombre ?: "Desconocido"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "El hijo \"$nombreHijo\" ha completado la tarea \"$tareaDesc\"",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { onValidarTarea(tarea) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784))
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Validar")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Validar")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { onNoValidarTarea(tarea) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Rechazar")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Rechazar")
                            }
                        }
                    }
                }
            }
        }

        if (expandedCard == "recompensas") {
            recompensasPendientesMap.forEach { (hijoId, recompensas) ->
                val nombreHijo = hijos.find { it.hijoId.toString() == hijoId }?.nombre ?: "Desconocido"
                recompensas.filter { it.estado == EstadoRecompensa.PENDIENTE.toString() }.forEach { recompensa ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Recompensa: ${recompensa.descripcion}")
                            Text("Puntos: ${recompensa.puntosNecesarios}")
                            Text("Hijo: $nombreHijo")
                        }
                    }
                }
            }
        }
    }
}