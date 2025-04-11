package edu.ucne.doers.presentation.tareas.padre

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.doers.data.local.entity.TareaEntity
import edu.ucne.doers.presentation.componentes.PadreNavBar
import edu.ucne.doers.presentation.navigation.Screen
import edu.ucne.doers.presentation.tareas.TareaUiState
import edu.ucne.doers.presentation.tareas.TareaViewModel
import edu.ucne.doers.presentation.tareas.components.TaskOverview

@Composable
fun TareasListScreen(
    viewModel: TareaViewModel = hiltViewModel(),
    goToAgregarTarea: () -> Unit,
    goToEditarTarea: (Int) -> Unit,
    onNavigateToRecompensas: () -> Unit,
    onNavigateToPerfil: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TareasBodyListScreen(
        uiState = uiState,
        goToAgregarTarea = goToAgregarTarea,
        onEdit = { tareaId -> goToEditarTarea(tareaId) },
        onDelete = { tarea -> viewModel.delete(tarea) },
        onNavigateToRecompensas = onNavigateToRecompensas,
        onNavigateToPerfil = onNavigateToPerfil
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareasBodyListScreen(
    uiState: TareaUiState,
    goToAgregarTarea: () -> Unit,
    onEdit: (Int) -> Unit,
    onDelete: (TareaEntity) -> Unit,
    onNavigateToRecompensas: () -> Unit,
    onNavigateToPerfil: () -> Unit
) {
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
                    IconButton(onClick = goToAgregarTarea) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar tarea",
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
                currentScreen = Screen.TareaList,
                onTareasClick = {},
                onRecompensasClick = onNavigateToRecompensas,
                onPerfilClick = onNavigateToPerfil
            )
        },
        content = { paddingValues ->
            TaskOverview(
                modifier = Modifier.padding(paddingValues),
                tareas = uiState.listaTareas,
                onEdit = onEdit,
                onDelete = onDelete
            )
        }
    )
}