package edu.ucne.doers.presentation.recompensa.padre

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberImagePainter
import edu.ucne.doers.R
import edu.ucne.doers.data.local.model.EstadoRecompensa
import edu.ucne.doers.presentation.componentes.PadreNavBar
import edu.ucne.doers.presentation.navigation.Screen
import edu.ucne.doers.presentation.recompensa.RecompensaUiState
import edu.ucne.doers.presentation.recompensa.RecompensaViewModel
import edu.ucne.doers.presentation.recompensa.toEntity
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecompensasListScreen(
    viewModel: RecompensaViewModel = hiltViewModel(),
    createRecompensa: () -> Unit,
    goToRecompensa: (Int) -> Unit,
    onNavigateToTareas: () -> Unit,
    onNavigateToPerfil: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val azulMar = Color(0xFF1976D2)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Doers",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            modifier = Modifier.padding(start = 11.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = createRecompensa) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar Recompensa",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = azulMar
                )
            )

        },
        bottomBar = {
            PadreNavBar(
                currentScreen = Screen.RecompensaList,
                onTareasClick = onNavigateToTareas,
                onRecompensasClick = {},
                onPerfilClick = onNavigateToPerfil
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn {
                items(uiState.recompensas) { recompensa ->
                    RecompensaRow(
                        recompensa = recompensa,
                        onClick = { },
                        onDelete = { viewModel.delete(recompensa.toEntity()) },
                        onEdit = { id -> goToRecompensa(id) },
                        onEstadoChange = { newEstado ->
                            viewModel.saveRecompensa(
                                recompensa.copy(estado = newEstado).toEntity()
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
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { isExpanded = !isExpanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
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
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Puntos: ${recompensa.puntosNecesarios}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
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
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Editar",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    OutlinedButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            "Eliminar",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = {
                            Text(
                                text = "Confirmar eliminación",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        text = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "¿Estás seguro que deseas eliminar esta tarea?",
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Icon(
                                    painter = painterResource(id = R.drawable.pensado_negro),
                                    contentDescription = "Pensando",
                                    modifier = Modifier.size(70.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        confirmButton = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = {
                                        onDelete()
                                        showDeleteDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError
                                    ),
                                    modifier = Modifier.size(130.dp, 50.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.trash),
                                            contentDescription = "Eliminar",
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Eliminar")
                                    }
                                }
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.background,
                        textContentColor = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}