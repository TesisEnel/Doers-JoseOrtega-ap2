package edu.ucne.doers.presentation.recompensa

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import edu.ucne.doers.data.local.entity.RecompensaEntity
import edu.ucne.doers.data.local.model.EstadoRecompensa
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecompensasListScreen(
    viewModel: RecompensaViewModel = hiltViewModel(),
    createRecompensa: () -> Unit,
    goToRecompensa: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Lista de Recompensas") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = createRecompensa) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar Recompensa")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            items(uiState.recompensas) { recompensa ->
                RecompensaItem(
                    recompensa = recompensa,
                    onEdit = { goToRecompensa(recompensa.recompensaId) },
                    onDelete = { viewModel.delete(recompensa) },
                    onToggleAvailability = { isAvailable ->
                        viewModel.updateAvailability(recompensa.recompensaId, isAvailable)
                    }
                )
            }
        }
    }
}

@Composable
fun RecompensaItem(
    recompensa: RecompensaEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleAvailability: (Boolean) -> Unit,
    viewModel: RecompensaViewModel = hiltViewModel()
) {
    var isExpanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Imagen
                if (recompensa.imagenURL.isNotEmpty()) {
                    Image(
                        painter = rememberImagePainter(File(recompensa.imagenURL)),
                        contentDescription = "Imagen de recompensa",
                        modifier = Modifier
                            .size(64.dp)
                            .padding(end = 8.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recompensa.descripcion,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Puntos: ${recompensa.puntosNecesarios}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Switch(
                    checked = recompensa.estado == EstadoRecompensa.DISPONIBLE,
                    onCheckedChange = { isAvailable ->
                        onToggleAvailability(isAvailable)
                    },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f).padding(end = 4.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                        Spacer(Modifier.width(4.dp))
                        Text("Editar")
                    }
                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.weight(1f).padding(start = 4.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        Spacer(Modifier.width(4.dp))
                        Text("Eliminar")
                    }
                }
            }
        }
    }
}