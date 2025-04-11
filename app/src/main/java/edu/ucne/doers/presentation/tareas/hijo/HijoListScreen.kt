package edu.ucne.doers.presentation.tareas.hijo

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.doers.AppReferences
import edu.ucne.doers.R
import edu.ucne.doers.presentation.hijos.HijoUiState
import edu.ucne.doers.presentation.hijos.HijoViewModel
import edu.ucne.doers.presentation.navigation.Screen
import edu.ucne.doers.presentation.recompensa.comp.HijoNavBar
import edu.ucne.doers.presentation.tareas.components.HorizontalFilter
import edu.ucne.doers.presentation.tareas.components.TareaCardHijo
import edu.ucne.doers.presentation.tareas.components.WelcomeModal

@Composable
fun HijoListScreen(
    viewModel: HijoViewModel = hiltViewModel(),
    onNavigateToRecompensas: () -> Unit,
    onNavigateToPerfil: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HijoBodyListScreen(
        uiState,
        viewModel,
        onNavigateToRecompensas = onNavigateToRecompensas,
        onNavigateToPerfil = onNavigateToPerfil

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HijoBodyListScreen(
    uiState: HijoUiState,
    viewModel: HijoViewModel,
    onNavigateToRecompensas: () -> Unit,
    onNavigateToPerfil: () -> Unit
) {
    val periodicidades by viewModel.periodicidadesDisponibles.collectAsState()
    var filtroSeleccionado by remember { mutableStateOf("Todas") }

    val context = LocalContext.current
    val appReferences = remember { AppReferences(context) }
    var showModal by rememberSaveable { mutableStateOf(appReferences.isFirstTime()) }

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }

        uiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        WelcomeModal(
            showModal = showModal,
            onDismiss = {
                showModal = false
                appReferences.setFirstTimeCompleted()
            },
            userName = uiState.nombre
        )

        Column(modifier = Modifier.fillMaxSize()) {
            CenterAlignedTopAppBar(
                title = { Text("Doers", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF1976D2)
                ),
                modifier = Modifier.shadow(elevation = 8.dp)
            )

            if (uiState.isLoading && uiState.ultimaAccionProcesada == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = Color(0xFF1976D2)
                    )
                }
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    HorizontalFilter(
                        options = periodicidades,
                        selectedOption = filtroSeleccionado,
                        onOptionSelected = {
                            filtroSeleccionado = it
                            viewModel.filtrarTareas(it)
                        },
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )

                    Text(
                        if (uiState.listaTareas.isEmpty()) "Tu padre o tutor no te ha asignado tareas"
                        else "Tareas asignadas por tu padre o tutor",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFF1976D2),
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    val tareasFiltradas = uiState.listaRecompensasFiltradas

                    if (tareasFiltradas.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.pantalla_de_espera),
                                    contentDescription = "No se encontró tarea",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(250.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    when {
                                        uiState.listaTareas.isEmpty() -> "No tienes tareas asignadas"
                                        filtroSeleccionado != "Todas" -> "No hay tareas con esta periodicidad"
                                        else -> "No hay tareas disponibles"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .weight(1f)
                        ) {
                            items(tareasFiltradas, key = { it.tareaId }) { tarea ->
                                TareaCardHijo(
                                    tarea = tarea,
                                    onCompletar = { viewModel.completarTarea(tarea.tareaId) }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                HijoNavBar(
                    currentScreen = Screen.TareaHijo,
                    onTareasClick = {},
                    onRecompensasClick = onNavigateToRecompensas,
                    onPerfilClick = onNavigateToPerfil
                )
            }
        }
    }
}