package edu.ucne.doers.presentation.recompensa

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import edu.ucne.doers.data.local.model.EstadoRecompensa
import edu.ucne.doers.presentation.navigation.Screen
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecompensasListScreen(
    viewModel: RecompensaViewModel = hiltViewModel(),
    createRecompensa: () -> Unit,
    goToRecompensa: (Int) -> Unit,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Recompensas", fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE0E6EB),
                    titleContentColor = Color.Black
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = createRecompensa,
                containerColor = Color(0xFF4A90E2),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear Recompensa")
            }
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentScreen = Screen.RecompensaList)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F2F5))
                .padding(innerPadding)
        ) {
            LazyColumn {
                items(uiState.recompensas) { recompensa ->
                    RecompensaRow(
                        recompensa = recompensa.toUiState(),
                        onClick = { },
                        onDelete = { viewModel.delete(recompensa) },
                        onEdit = { id -> goToRecompensa(id) },
                        onEstadoChange = { newEstado ->
                            viewModel.saveRecompensa(
                                recompensa.copy(estado = newEstado.toString())
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RecompensaRow(
    recompensa: RecompensaUiState,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onEdit: (Int) -> Unit,
    onEstadoChange: (EstadoRecompensa) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { isExpanded = !isExpanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (recompensa.imagenURL.isNotEmpty()) {
                    Image(
                        painter = rememberImagePainter(File(recompensa.imagenURL)),
                        contentDescription = "Imagen de recompensa",
                        modifier = Modifier
                            .size(64.dp)
                            .padding(end = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = recompensa.descripcion,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Puntos: ${recompensa.puntosNecesarios}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Switch(
                    checked = recompensa.estado == EstadoRecompensa.DISPONIBLE,
                    onCheckedChange = { isChecked ->
                        val newEstado = if (isChecked) EstadoRecompensa.DISPONIBLE else EstadoRecompensa.AGOTADA
                        onEstadoChange(newEstado)
                    }
                )
            }

            if (isExpanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(onClick = { onEdit(recompensa.recompensaId) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar"
                        )
                        Text("Editar")
                    }
                    OutlinedButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar"
                        )
                        Text("Eliminar")
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, currentScreen: Screen) {
    NavigationBar(
        containerColor = Color(0xFFE0E6EB),
        contentColor = Color.Black
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Checklist, contentDescription = "Tarea") },
            label = { Text("Tarea") },
            selected = currentScreen == Screen.Padre,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Star, contentDescription = "Recompensas") },
            label = { Text("Recompensas") },
            selected = currentScreen == Screen.RecompensaList,
            onClick = {  }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") },
            selected = currentScreen == Screen.Padre,
            onClick = {
                navController.navigate(Screen.Padre) {
                    popUpTo(Screen.Padre) { inclusive = true }
                }
            }
        )
    }
}