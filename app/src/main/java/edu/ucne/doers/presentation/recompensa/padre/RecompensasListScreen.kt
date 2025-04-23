package edu.ucne.doers.presentation.recompensa.padre

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
import edu.ucne.doers.data.local.entity.RecompensaEntity
import edu.ucne.doers.presentation.componentes.PadreNavBar
import edu.ucne.doers.presentation.navigation.Screen
import edu.ucne.doers.presentation.recompensa.RecompensaUiState
import edu.ucne.doers.presentation.recompensa.RecompensaViewModel
import edu.ucne.doers.presentation.recompensa.components.RecompensasOverview

@Composable
fun RecompensasListScreen(
    viewModel: RecompensaViewModel = hiltViewModel(),
    goToAgregarRecompensa: () -> Unit,
    goToEditarRecompensa: (Int) -> Unit,
    onNavigateToTareas: () -> Unit,
    onNavigateToPerfil: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    RecompensasBodyListScreen(
        uiState = uiState,
        goToAgregarRecompensa = goToAgregarRecompensa,
        onEdit = { recompensaId -> goToEditarRecompensa(recompensaId) },
        onDelete = { recompensa -> viewModel.delete(recompensa) },
        onNavigateToTareas = onNavigateToTareas,
        onNavigateToPerfil = onNavigateToPerfil
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecompensasBodyListScreen(
    uiState: RecompensaUiState,
    goToAgregarRecompensa: () -> Unit,
    onEdit: (Int) -> Unit,
    onDelete: (RecompensaEntity) -> Unit,
    onNavigateToTareas: () -> Unit,
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
                    IconButton(onClick = goToAgregarRecompensa) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar Recompensa",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = azulMar)
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
        content = { paddingValues ->
            RecompensasOverview(
                modifier = Modifier.padding(paddingValues),
                recompensas = uiState.listaRecompensas,
                onEdit = onEdit,
                onDelete = onDelete
            )
        }
    )
}