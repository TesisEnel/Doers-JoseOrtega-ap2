package edu.ucne.doers.presentation.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import edu.ucne.doers.data.local.entity.RecompensaEntity
import java.io.File

@Composable
fun ImagenRecompensa(recompensa: RecompensaEntity) {
    if (recompensa.imagenURL.isNotEmpty()) {
        Image(
            painter = rememberAsyncImagePainter(File(recompensa.imagenURL)),
            contentDescription = "Imagen de recompensa",
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
        )
    } else {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE0E0E0)) // Fondo gris para imagen faltante
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
        )
    }
}