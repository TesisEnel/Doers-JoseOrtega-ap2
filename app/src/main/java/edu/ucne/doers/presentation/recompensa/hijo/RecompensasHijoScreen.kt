package edu.ucne.doers.presentation.recompensa.hijo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.doers.data.local.model.EstadoRecompensa
import edu.ucne.doers.presentation.componentes.ImagenRecompensa
import edu.ucne.doers.presentation.navigation.Screen
import edu.ucne.doers.presentation.recompensa.RecompensaUiState
import edu.ucne.doers.presentation.recompensa.RecompensaViewModel
import edu.ucne.doers.presentation.recompensa.comp.HijoNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecompensasHijoScreen(
    viewModel: RecompensaViewModel = hiltViewModel(),
    padreId: String,
    onNavigateToTareas: () -> Unit,
    onNavigateToPerfil: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val azulCielo = Color(0xFF1976D2)

    LaunchedEffect(padreId) {
        viewModel.loadRecompensas()
    }

    /*Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Doers",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = azulCielo
                )
            )
        },
        bottomBar = {
            HijoNavBar(
                currentScreen = Screen.RecompensaHijo,
                onTareasClick = onNavigateToTareas,
                onRecompensasClick = {},
                onPerfilClick = onNavigateToPerfil
            )
        },
        containerColor = MaterialTheme.colorScheme.onSurface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                val availableRecompensas = uiState.recompensas.filter { it.estado == EstadoRecompensa.DISPONIBLE }
                val pairedRecompensas = availableRecompensas.chunked(2)
                items(pairedRecompensas) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        pair.forEach { recompensa ->
                            RecompensaRow(
                                recompensa = recompensa,
                                modifier = Modifier
                                    .weight(1f)
                            )
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecompensaRow(
    recompensa: RecompensaUiState,
    modifier: Modifier = Modifier
) {
    if (recompensa.estado == EstadoRecompensa.DISPONIBLE) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImagenRecompensa(recompensa)
                Text(
                    text = recompensa.descripcion,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF0288D1)
                )
                Text(
                    text = "${recompensa.puntosNecesarios} Puntos",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp
                    ),
                    textAlign = TextAlign.Center,
                    color = Color(0xFFFF5722)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { /* Acción para reclamar */ },
                    modifier = Modifier
                        .width(120.dp)
                        .height(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD740),
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "¡Reclamar!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
    
     */
}