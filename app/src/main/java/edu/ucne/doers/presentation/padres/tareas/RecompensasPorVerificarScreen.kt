package edu.ucne.doers.presentation.padres.tareas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.ucne.doers.data.local.model.EstadoCanjeo
import edu.ucne.doers.presentation.padres.PadreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecompensasPorVerificarScreen(
    onNavigateToPerfil: () -> Unit,
    padreViewModel: PadreViewModel = hiltViewModel()
) {
    val hijos by padreViewModel.hijos.collectAsState()
    val canjeosFiltrados by padreViewModel.canjeosFiltrados.collectAsState()
    val canjeos by padreViewModel.canjeoHijo.collectAsState()
    val recompensas by padreViewModel.recompensas.collectAsState()
    val selectedIndex = remember { mutableIntStateOf(0) }

    val azulMar = Color(0xFF1976D2)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Doers",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            modifier = Modifier.padding(end = 50.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateToPerfil) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = azulMar)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Recompensas pendientes a verificación",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .fillMaxWidth()
            )

            Text(
                text = "Filtrar por hijo",
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            DropdownFiltroHijos(
                items = listOf("Todos") + hijos.map { it.nombre },
                selectedIndex = selectedIndex.intValue,
                onItemSelected = {
                    selectedIndex.intValue = it
                    val hijoId = if (it == 0) null else hijos.getOrNull(it - 1)?.hijoId
                    padreViewModel.filtrarRecompensasPorHijo(hijoId)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            val hayPendientesGlobal = canjeos.any { it.estado == EstadoCanjeo.PENDIENTE_VERIFICACION }

            when {
                !hayPendientesGlobal -> {
                    Text("No hay recompensas pendientes a verificar",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                canjeosFiltrados.isEmpty() -> {
                    Text("No hay recompensas pendientes para este hijo",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                else -> {
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(canjeosFiltrados) { canjeo ->
                            val hijo = hijos.find { it.hijoId == canjeo.hijoId }
                            val recompensa = recompensas.find { it.recompensaId == canjeo.recompensaId }

                            RewardVerificationCard(
                                hijoNombre = hijo?.nombre ?: "Desconocido",
                                recompensaDescripcion = recompensa?.descripcion ?: "Recompensa desconocida",
                                puntos = recompensa?.puntosNecesarios ?: 0,
                                onValidar = { padreViewModel.validarRecompensa(canjeo) },
                                onRechazar = { padreViewModel.rechazarRecompensa(canjeo) }
                            )
                        }
                    }
                }
            }
        }
    }
}