package edu.ucne.doers.presentation.tareas.hijo

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import edu.ucne.doers.R
import edu.ucne.doers.data.local.model.PeriodicidadTarea
import edu.ucne.doers.presentation.navigation.Screen
import edu.ucne.doers.presentation.tareas.components.HorizontalFilter
import edu.ucne.doers.presentation.tareas.components.TareaCardHijo
import edu.ucne.doers.presentation.tareas.components.WelcomeModal
import kotlin.math.min

@Composable
fun HijoListScreen(
    viewModel: HijoViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HijoBodyListScreen(
        uiState,
        viewModel,
        navController
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HijoBodyListScreen(
    uiState: HijoUiState,
    viewModel: HijoViewModel,
    navController: NavHostController
) {
    val periodicidades = remember {
        listOf("Todas") + PeriodicidadTarea.entries.map { it.nombreMostrable }
    }
    var filtroSeleccionado by remember { mutableStateOf("Todas") }

    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    var showModal by remember { mutableStateOf(isPortrait) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val boxConstraints = this.constraints

        Box(modifier = Modifier.fillMaxSize()) {
            if (showModal) {
                WelcomeModal(
                    showModal = showModal,
                    onDismiss = { showModal = false },
                    userName = uiState.nombre
                )
            }

            Column(modifier = Modifier.fillMaxSize()) {
                CenterAlignedTopAppBar(
                    title = { Text("Doers", color = Color.White, fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF1976D2)
                    ),
                    modifier = Modifier.shadow(elevation = 8.dp)
                )

                if (uiState.isLoading) {
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
                            onOptionSelected = { filtroSeleccionado = it },
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )

                        Text(
                            "Tareas asignadas por tu padre o tutor",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color(0xFF1976D2),
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )

                        val tareasFiltradas =
                            remember(uiState.listaTareas, filtroSeleccionado) {
                                if (filtroSeleccionado == "Todas") {
                                    uiState.listaTareas
                                } else {
                                    uiState.listaTareas.filter { it.periodicidad?.nombreMostrable == filtroSeleccionado }
                                }
                            }

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
                                        "No se han encontrado tareas con este estado",
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
                }
                BottomNavigationBar(navController, Screen.TareaHijo)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentScreen: Screen
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Checklist, contentDescription = "Tareas") },
            label = { Text("Tareas") },
            selected = currentScreen == Screen.TareaHijo,
            onClick = {}
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Star, contentDescription = "Recompensas") },
            label = { Text("Recompensas") },
            selected = currentScreen == Screen.RecompensaHijo,
            onClick = { navController.navigate(Screen.RecompensaHijo) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") },
            selected = currentScreen == Screen.Hijo,
            onClick = { navController.navigate(Screen.Hijo) }
        )
    }
}