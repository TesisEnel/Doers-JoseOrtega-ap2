package edu.ucne.doers.presentation.hijos

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import edu.ucne.doers.presentation.navigation.Screen
import edu.ucne.doers.presentation.padres.PadreViewModel
import edu.ucne.doers.presentation.recompensa.comp.HijoNavBar

@Composable
fun HijoScreen(
    hijoViewModel: HijoViewModel,
    padreViewModel: PadreViewModel,
    onNavigateToTareas: () -> Unit,
    onNavigateToRecompensas: () -> Unit
) {
    val hijoUiState by hijoViewModel.uiState.collectAsState()
    val padreUiState by padreViewModel.uiState.collectAsState()
    val colors = MaterialTheme.colorScheme
    var editedName by remember { mutableStateOf(hijoUiState.nombre ?: "Hijo") }
    var selectedPhoto by remember { mutableStateOf(hijoUiState.fotoPerfil) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var tempSelectedPhoto by remember { mutableStateOf(hijoUiState.fotoPerfil) }

    if (hijoUiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            bottomBar = {
                HijoNavBar(
                    currentScreen = Screen.Hijo,
                    onTareasClick = onNavigateToTareas,
                    onRecompensasClick = onNavigateToRecompensas,
                    onPerfilClick = {}
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.background)
                        .padding(innerPadding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val fotos = listOf("personaje_1", "personaje_2", "personaje_3", "personaje_4", "personaje_5")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = "android.resource://edu.ucne.doers/drawable/${hijoUiState.fotoPerfil}",
                                contentDescription = "Foto de perfil",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(colors.onSurface.copy(alpha = 0.1f))
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = hijoUiState.nombre ?: "Hijo",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                IconButton(
                                    onClick = {
                                        editedName = hijoUiState.nombre ?: "Hijo"
                                        showEditProfileDialog = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = "Editar nombre",
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Cuenta de ",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = colors.onSurfaceVariant
                                    )
                                )
                                Text(
                                    text = "Hijo 👦👧",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = colors.primary
                                    )
                                )
                            }
                        }
                    }
                    if (showEditProfileDialog) {
                        AlertDialog(
                            onDismissRequest = { showEditProfileDialog = false },
                            title = {
                                Text(
                                    text = "Editar Perfil",
                                    fontWeight = FontWeight.Bold,
                                    color = colors.primary,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            },
                            text = {
                                Column {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        fotos.forEach { photoName ->
                                            Box(
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .clickable {
                                                        tempSelectedPhoto = photoName
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AsyncImage(
                                                    model = "android.resource://edu.ucne.doers/drawable/$photoName",
                                                    contentDescription = "Foto $photoName",
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .clip(CircleShape)
                                                        .background(
                                                            if (tempSelectedPhoto == photoName) colors.surfaceVariant.copy(alpha = 0.5f)
                                                            else colors.background
                                                        )
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    OutlinedTextField(
                                        value = editedName,
                                        onValueChange = { editedName = it },
                                        label = { Text("Nombre") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        selectedPhoto = tempSelectedPhoto
                                        selectedPhoto?.let { hijoViewModel.actualizarFotoPerfil(it) }
                                        hijoViewModel.actualizarNombre(editedName)
                                        showEditProfileDialog = false
                                    }
                                ) {
                                    Text("Guardar")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { showEditProfileDialog = false }
                                ) {
                                    Text("Cancelar")
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = colors.surface),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        val clipboardManager = LocalClipboardManager.current
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                color = colors.onSurface,
                                                fontWeight = FontWeight.Bold
                                            )
                                        ) {
                                            append("Código de Sala de: ")
                                        }
                                        withStyle(
                                            style = SpanStyle(
                                                color = colors.primary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        ) {
                                            append("${padreUiState.nombre}")
                                        }
                                    },
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = padreUiState.codigoSala ?: "N/A",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp,
                                        color = colors.secondary
                                    )
                                )
                            }
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(
                                        AnnotatedString(
                                            padreUiState.codigoSala ?: ""
                                        )
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = "Copiar Código",
                                    tint = colors.secondary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    TextButton(onClick = { hijoViewModel.cerrarSesion() }) {
                        Text(
                            text = "Cerrar sesión (TEMP)",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}