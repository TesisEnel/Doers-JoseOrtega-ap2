package edu.ucne.doers.presentation.tareas.hijo

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun HijoBodyListScreen(
    uiState: HijoUiState,
    viewModel: HijoViewModel,
    onNavigateToRecompensas: () -> Unit,
    onNavigateToPerfil: () -> Unit
) {
    val periodicidades by viewModel.periodicidadesDisponibles.collectAsState()
    val tareasFiltradas = uiState.listaTareasFiltradas
    var filtroSeleccionado by remember { mutableStateOf("Todas") }

    val context = LocalContext.current
    val appReferences = remember { AppReferences(context) }
    var showModal by rememberSaveable { mutableStateOf(appReferences.isFirstTime()) }

    val amarilloMoneda = Color(0xFFFFA000)

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

    WelcomeModal(
        showModal = showModal,
        onDismiss = {
            showModal = false
            appReferences.setFirstTimeCompleted()
        },
        userName = uiState.nombre
    )

    Scaffold(
        bottomBar = {
            HijoNavBar(
                currentScreen = Screen.TareaHijo,
                onTareasClick = {},
                onRecompensasClick = onNavigateToRecompensas,
                onPerfilClick = onNavigateToPerfil
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Fondo superior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(amarilloMoneda),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier.padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.TaskAlt,
                        contentDescription = "Icono Tareas",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tareas Pendientes a realizar",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Tarjeta blanca principal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 140.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (uiState.isLoading && uiState.ultimaAccionProcesada == null) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = Color(0xFF1976D2)
                            )
                        }
                    } else {
                        // Filtro centrado
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 4.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            HorizontalFilter(
                                options = periodicidades,
                                selectedOption = filtroSeleccionado,
                                onOptionSelected = {
                                    filtroSeleccionado = it
                                    viewModel.filtrarTareas(it)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (tareasFiltradas.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
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
                        } else {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                                    .heightIn(min = 200.dp, max = 600.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(0.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Transparent)
                                        .padding(16.dp)
                                ) {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .background(Color.Transparent),
                                        verticalArrangement = Arrangement.spacedBy(16.dp),
                                        contentPadding = PaddingValues(vertical = 8.dp)
                                    ) {
                                        items(tareasFiltradas, key = { it.tareaId }) { tarea ->
                                            TareaCardHijo(
                                                tarea = tarea,
                                                onCompletar = {
                                                    viewModel.completarTarea(tarea.tareaId)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}