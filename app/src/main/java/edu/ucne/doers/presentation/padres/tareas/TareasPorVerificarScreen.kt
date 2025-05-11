package edu.ucne.doers.presentation.padres.tareas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.ucne.doers.data.local.model.EstadoTareaHijo
import edu.ucne.doers.presentation.padres.PadreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareasPorVerificarScreen(
    onNavigateToPerfil: () -> Unit,
    padreViewModel: PadreViewModel = hiltViewModel()
) {
    val hijos by padreViewModel.hijos.collectAsState()
    val tareasFiltradas by padreViewModel.tareasFiltradas.collectAsState()
    val tareas by padreViewModel.tareas.collectAsState()
    val tareasHijo by padreViewModel.tareasHijo.collectAsState()
    val selectedIndex = remember { mutableIntStateOf(0) }

    val azulMar = Color(0xFF1976D2)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
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
                    IconButton(onClick = onNavigateToPerfil) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = azulMar)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Tareas pendientes a verificación",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .fillMaxWidth()
            )

            Text(
                text = "Filtrar por hijo",
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            DropdownFiltroHijos(
                items = listOf("Todos") + hijos.map { it.nombre },
                selectedIndex = selectedIndex.intValue,
                onItemSelected = {
                    selectedIndex.intValue = it
                    val hijoId = if (it == 0) null else hijos.getOrNull(it - 1)?.hijoId
                    padreViewModel.filtrarTareasPorHijo(hijoId)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            val tareasPendientesGlobales = tareasHijo.any { it.estado == EstadoTareaHijo.PENDIENTE_VERIFICACION }

            when {
                !tareasPendientesGlobales -> {
                    Text("No hay tareas pendientes a verificar", style = MaterialTheme.typography.bodyMedium)
                }

                tareasFiltradas.isEmpty() -> {
                    Text("No hay tareas pendientes para este hijo", style = MaterialTheme.typography.bodyMedium)
                }

                else -> {
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(tareasFiltradas) { tareaHijo ->
                            val hijo = hijos.find { it.hijoId == tareaHijo.hijoId }
                            val tarea = tareas.find { it.tareaId == tareaHijo.tareaId }

                            TaskVerificationCard(
                                hijoNombre = hijo?.nombre ?: "Desconocido",
                                tareaDescripcion = tarea?.descripcion ?: "Tarea desconocida",
                                onValidar = { padreViewModel.validarTarea(tareaHijo) },
                                onRechazar = { padreViewModel.rechazarTarea(tareaHijo) }
                            )
                        }
                    }
                }
            }
        }
    }
}