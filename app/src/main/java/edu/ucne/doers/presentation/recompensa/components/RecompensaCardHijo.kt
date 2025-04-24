package edu.ucne.doers.presentation.recompensa.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.ucne.doers.data.local.entity.RecompensaEntity
import edu.ucne.doers.presentation.componentes.ImagenRecompensa

@Composable
fun RecompensaCardHijo(
    recompensa: RecompensaEntity,
    onReclamar: () -> Unit
) {
    val amarillo = Color(0xFFFFA000)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen
        ImagenRecompensa(recompensa)

        Spacer(modifier = Modifier.width(16.dp))

        // Descripción + Precio
        Row(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = recompensa.descripcion,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "$${recompensa.puntosNecesarios}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = amarillo
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Botón Reclamar
        Button(
            onClick = onReclamar,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A90E2),
                contentColor = Color.White
            ),
            modifier = Modifier
                .height(36.dp)
                .width(90.dp)
        ) {
            Text(
                text = "Mostrar",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}