package edu.ucne.doers.presentation.tareas.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.ucne.doers.R

@Composable
fun EmptyStateView(imageSize: Float) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomCenter) {
            Image(
                painter = painterResource(id = R.drawable.chica_lista_vacia_removebg),
                contentDescription = "Lista vacía",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(imageSize.dp)
                    .padding(bottom = 8.dp)
            )
            Text(
                "\"Tu padre o tutor no ha activado ninguna tarea\"",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}