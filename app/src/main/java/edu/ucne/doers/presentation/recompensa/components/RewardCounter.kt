package edu.ucne.doers.presentation.recompensa.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.ucne.doers.R
import edu.ucne.doers.data.local.model.EstadoCanjeo
import edu.ucne.doers.data.local.model.EstadoRecompensa

@Composable
fun RewardCounter(
    label: String,
    count: Int,
    estado: EstadoRecompensa
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .height(150.dp)
            .width(150.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = count.toString(),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
                color = when (estado) {
                    EstadoRecompensa.COMPLETADA -> if(count > 0) Color(0xFF388E3C) else Color.Red
                    EstadoRecompensa.PENDIENTE -> if(count > 0) Color(0xFFFFA000) else Color.Red
                    else -> Color.Black
                }
            )
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            when (estado) {
                EstadoRecompensa.COMPLETADA -> {
                    Icon(
                        painter = painterResource(id = R.drawable.check),
                        contentDescription = "Completado",
                        modifier = Modifier.size(30.dp).padding(top = 4.dp),
                        tint = estado.color(),
                        )
                }
                EstadoRecompensa.PENDIENTE -> {
                    Icon(
                        painter = painterResource(id = R.drawable.exclamation_mark),
                        contentDescription = "Pendiente",
                        modifier = Modifier.size(30.dp).padding(top = 4.dp),
                        tint = estado.color()
                    )
                }
                else -> { }
            }
        }
    }
}