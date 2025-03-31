package edu.ucne.doers.presentation.hijos

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import edu.ucne.doers.R
import edu.ucne.doers.presentation.navigation.Screen
import edu.ucne.doers.presentation.padres.PadreViewModel

@Composable
fun HijoScreen(
    hijoViewModel: HijoViewModel,
    padreViewModel: PadreViewModel,
    navController: NavController
) {
    val hijoUiState by hijoViewModel.uiState.collectAsState()
    val padreUiState by padreViewModel.uiState.collectAsState()
    val colors = MaterialTheme.colorScheme
    var showEditNameDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(hijoUiState.nombre ?: "Hijo") }

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
                BottomNavigationBar(navController, Screen.Hijo)
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
                                model = R.drawable.icono_hijo,
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
                                        showEditNameDialog = true
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
                    if (showEditNameDialog) {
                        AlertDialog(
                            onDismissRequest = { showEditNameDialog = false },
                            title = {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Editar Nombre")
                                }
                            },
                            text = {
                                TextField(
                                    value = editedName,
                                    onValueChange = { editedName = it },
                                    label = { Text("Tu nombre") },
                                    singleLine = true
                                )
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        hijoViewModel.actualizarNombre(editedName)
                                        showEditNameDialog = false
                                    }
                                ) {
                                    Text("Aceptar")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { showEditNameDialog = false }
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
                        val context = LocalContext.current
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
    val colors = MaterialTheme.colorScheme
    NavigationBar(
        containerColor = colors.surface,
        contentColor = colors.onSurface
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
            onClick = { navController.navigate(Screen.RecompensaHijo) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") },
            selected = currentScreen == Screen.Hijo,
            onClick = { }
        )
    }
}