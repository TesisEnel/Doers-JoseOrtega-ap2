package edu.ucne.doers.presentation.padres.tareas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val tareasPendientes = tareasHijo.count { it.estado == EstadoTareaHijo.PENDIENTE_VERIFICACION }
    val canjeosPendientes = canjeos.count { it.estado == EstadoCanjeo.PENDIENTE_VERIFICACION }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Resumen de Actividades",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4A90E2),
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta de Tareas
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { goToTareasVerificar() },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                elevation = CardDefaults.cardElevation(6.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.TaskAlt,
                        contentDescription = null,
                        tint = Color(0xFFFFA000),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tareas por Verificar",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A90E2),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "$tareasPendientes tareas",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }

            // Tarjeta de Recompensas
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { goToRecompensasVerificar() },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE)),
                elevation = CardDefaults.cardElevation(6.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFF4A90E2),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Recompensas Pendientes",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFA000),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "$canjeosPendientes recompensas",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
