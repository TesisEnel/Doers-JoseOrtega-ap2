package edu.ucne.doers.presentation.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import edu.ucne.doers.data.local.entity.RecompensaEntity
import edu.ucne.doers.presentation.recompensa.RecompensaUiState
import java.io.File

@Composable
fun ImagenRecompensa(
    recompensa: RecompensaEntity
){
    if (recompensa.imagenURL.isNotEmpty()) {
        Image(
            painter = rememberImagePainter(File(recompensa.imagenURL)),
            contentDescription = "Imagen de recompensa",
            modifier = Modifier
                .size(60.dp)
                .padding(end = 8.dp)
        )
    } else {
        Spacer(
            modifier = Modifier
                .size(60.dp)
                .padding(end = 8.dp)
        )
    }
}