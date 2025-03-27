package edu.ucne.doers.presentation.tareas.padre

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.doers.presentation.tareas.components.PeriodoDropdownMenu

@Composable
fun TareaScreen(
    viewModel: TareaViewModel = hiltViewModel(),
    goBackToPantallaTareas: () -> Unit,
    tareaId: Int
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TareaBodyScreen(
        viewModel,
        uiState,
        goBackToPantallaTareas,
        tareaId
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareaBodyScreen(
    viewModel: TareaViewModel,
    uiState: TareaUiState,
    goBackToPantallaTareas: () -> Unit,
    tareaId: Int
) {
    LaunchedEffect(tareaId) {
        if (tareaId > 0)
            viewModel.find(tareaId)
    }

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
                            modifier = Modifier.padding(end = 50.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = goBackToPantallaTareas) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = azulMar
                )
            )
        },
        content = { paddingValues ->
            CreateTaskForm(
                modifier = Modifier.padding(paddingValues),
                uiState = uiState,
                viewModel = viewModel,
                goBackToPantallaTareas = goBackToPantallaTareas,
                tareaId = tareaId
            )
        }

    )
}


@Composable
fun CreateTaskForm(
    modifier: Modifier,
    uiState: TareaUiState,
    viewModel: TareaViewModel,
    goBackToPantallaTareas: () -> Unit,
    tareaId: Int = 0
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = if (tareaId > 0) "Modificar Tarea" else "Crear Nueva Tarea",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = uiState.descripcion,
            onValueChange = viewModel::onDescripcionChange,
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.errorMessage?.contains("descripción") == true,
            supportingText = {
                if (uiState.errorMessage?.contains("descripción") == true) {
                    Text(text = uiState.errorMessage ?: "", color = Color.Red)
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = uiState.puntos.toString(),
            onValueChange = { puntos ->
                puntos.toIntOrNull()?.let { viewModel.onPuntosChange(it) }
            },
            label = { Text("Puntos") },
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.errorMessage?.contains("puntos") == true,
            supportingText = {
                if (uiState.errorMessage?.contains("puntos") == true) {
                    Text(text = uiState.errorMessage, color = Color.Red)
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))
        PeriodoDropdownMenu(
            selectedPeriodo = uiState.periodicidad,
            onPeriodoSelected = viewModel::onPeriodicidadChange
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            label = { Text("Estado") },
            value = uiState.estado.name,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (uiState.padreId.isNotEmpty()) {"Padre ID: ${uiState.padreId}"}
            else {"Padre ID: No encontrado"},
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        if (uiState.errorMessage != null && uiState.errorMessage.contains("requeridos")) {
            Text(
                text = uiState.errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (tareaId > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            viewModel.save()
                            goBackToPantallaTareas()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Modificar",
                            tint = Color.White
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = "Modificar", color = Color.White)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            viewModel.new()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Nuevo",
                            tint = Color.White
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Nuevo", color = Color.White)
                    }
                    Button(
                        onClick = {
                            viewModel.save()
                            goBackToPantallaTareas()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Guardar",
                            tint = Color.White
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = "Guardar", color = Color.White)
                    }
                }
            }
        }
    }
}
