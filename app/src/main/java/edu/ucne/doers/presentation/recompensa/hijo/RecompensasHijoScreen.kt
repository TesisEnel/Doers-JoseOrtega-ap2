package edu.ucne.doers.presentation.recompensa.hijo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.doers.data.local.model.EstadoRecompensa
import edu.ucne.doers.presentation.componentes.ImagenRecompensa
import edu.ucne.doers.presentation.hijos.HijoViewModel
import edu.ucne.doers.presentation.navigation.Screen
import edu.ucne.doers.presentation.recompensa.RecompensaUiState
import edu.ucne.doers.presentation.recompensa.RecompensaViewModel
import edu.ucne.doers.presentation.recompensa.comp.HijoNavBar
import edu.ucne.doers.presentation.recompensa.toUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecompensasHijoScreen(
    viewModel: RecompensaViewModel = hiltViewModel(),
    padreId: String,
    hijoViewModel: HijoViewModel,
    onNavigateToTareas: () -> Unit,
    onNavigateToPerfil: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val hijoUiState by hijoViewModel.uiState.collectAsStateWithLifecycle()
    val azulCielo = Color(0xFF1976D2)

    LaunchedEffect(padreId) {
        viewModel.loadRecompensas()
        hijoViewModel.loadSaldoActual()
        hijoViewModel.getHijosByPadre(padreId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Recompensas",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${hijoUiState.saldoActual} 🪙",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
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
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(uiState.recompensas.filter {
                    it.estado == EstadoRecompensa.DISPONIBLE
                }) { recompensa ->
                    RecompensaCard(
                        recompensa = recompensa.toUiState()
                    )
                }
            }
        }
    }
}

@Composable
fun RecompensaCard(
    recompensa: RecompensaUiState,
    viewModel: HijoViewModel = hiltViewModel(),
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImagenRecompensa(recompensa)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recompensa.descripcion,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "${recompensa.puntosNecesarios} 🪙",
                    fontSize = 16.sp,
                    color = Color(0xFFFF5722)
                )
            }
            Button(
                onClick = {
                    viewModel.reclamarRecompensa(recompensa.recompensaId)
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD740),
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Reclamar",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
