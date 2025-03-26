package edu.ucne.doers.presentation.recompensa.hijo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import edu.ucne.doers.data.local.model.EstadoRecompensa
import edu.ucne.doers.presentation.hijos.HijoViewModel
import edu.ucne.doers.presentation.navigation.Screen
import edu.ucne.doers.presentation.recompensa.RecompensaUiState
import edu.ucne.doers.presentation.recompensa.RecompensaViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecompensasHijoScreen(
    viewModel: RecompensaViewModel = hiltViewModel(),
    hijoViewModel: HijoViewModel = hiltViewModel(),
    padreId: String,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(padreId) {
        viewModel.loadRecompensas()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recompensas", fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentScreen = Screen.RecompensaHijo)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val pairedRecompensas = uiState.recompensas.chunked(2)
                items(pairedRecompensas) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pair.forEach { recompensa ->
                            RecompensaRow(
                                recompensa = recompensa,
                                viewModel = viewModel,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp)
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
    viewModel: RecompensaViewModel,
    hijoViewModel: HijoViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    if (recompensa.estado == EstadoRecompensa.DISPONIBLE) {
        Card(
            modifier = modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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

                Text(
                    text = recompensa.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${recompensa.puntosNecesarios} pts",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(100.dp)
                        .height(36.dp),
                    contentPadding = PaddingValues(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    )
                ) {
                    Text("Reclamar")
                }
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
            onClick = { navController.navigate(Screen.TareaHijo) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Star, contentDescription = "Recompensas") },
            label = { Text("Recompensas") },
            selected = currentScreen == Screen.RecompensaHijo,
            onClick = {}
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") },
            selected = currentScreen == Screen.Hijo,
            onClick = { navController.navigate(Screen.Hijo) }
        )
    }
}