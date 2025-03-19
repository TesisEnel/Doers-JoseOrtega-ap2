package edu.ucne.doers.presentation.padres

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import edu.ucne.doers.presentation.navigation.Screen

@Composable
fun PadreScreen(
    viewModel: PadreViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController, Screen.Padre)
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF0F2F5))
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
                            if (!uiState.fotoPerfil.isNullOrEmpty()) {
                                AsyncImage(
                                    model = uiState.fotoPerfil,
                                    contentDescription = "Foto de perfil del padre",
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = uiState.nombre?.firstOrNull()?.toString() ?: "P",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = uiState.nombre ?: "Padre",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = Color.Black
                            )
                            uiState.email?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.DarkGray
                                )
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
                                        color = Color.Gray
                                    )
                                )
                                Text(
                                    text = "Padre 👨‍👩‍👧‍👦",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF1E88E5)
                                    )
                                )
                            }
                        }
                        IconButton(
                            onClick = { showDialog = true },
                            modifier = Modifier
                                .size(40.dp)
                                .padding(end = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Cerrar sesión",
                                tint = Color.Gray
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                                    text = "Código de Sala",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = uiState.codigoSala ?: "N/A",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp,
                                        color = Color(0xFF2E7D32)
                                    )
                                )
                            }
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(uiState.codigoSala ?: ""))
                                    Toast.makeText(context, "Código copiado", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = "Copiar Código",
                                    tint = Color(0xFF2E7D32)
                                )
                            }
                            IconButton(
                                onClick = {
                                    val mensaje = """
                                        🎯 ¡${uiState.nombre} te invita a unirte a su sala en Doers! 👨‍👩‍👧‍👦
                                        Ha creado una sala en Doers para motivarnos a completar tareas y ganar recompensas. 🎁
                                        🔑 Código de la sala: ${uiState.codigoSala}
                                        📲 Descarga la app y usa este código para unirte.
                                        🚀 ¡Únete ahora y empieza a ganar recompensas divertidas! 🎉
                                        """.trimIndent()

                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, mensaje)
                                    }
                                    context.startActivity(
                                        Intent.createChooser(
                                            intent,
                                            "Compartir Código"
                                        )
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Share,
                                    contentDescription = "Compartir Código",
                                    tint = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            icon = {
                if (!uiState.fotoPerfil.isNullOrEmpty()) {
                    AsyncImage(
                        model = uiState.fotoPerfil,
                        contentDescription = "Foto de perfil del padre",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.nombre?.firstOrNull()?.toString() ?: "P",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                    }
                }
            },
            title = {
                Text(
                    "Cerrar Sesión",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = { Text("¿Estás seguro de que quieres salir?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.signOut()
                        navController.navigate(Screen.Home) {
                            popUpTo(Screen.Padre) { inclusive = true }
                        }
                    }
                ) {
                    Text("Sí, cerrar sesión", color = Color(0xFF2E7D32))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No, volver atrás", color = Color.Gray)
                }
            },
            containerColor = Color(0xFFFAF8F1),
            titleContentColor = Color(0xFF2E7D32),
            textContentColor = Color(0xFF333333),
            shape = MaterialTheme.shapes.medium,
        )
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentScreen: Screen
) {
    NavigationBar(
        containerColor = Color(0xFFE0E6EB),
        contentColor = Color.Black
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Checklist, contentDescription = "Tarea") },
            label = { Text("Tarea") },
            selected = currentScreen == Screen.PantallaTareas,
            onClick = { navController.navigate(Screen.PantallaTareas) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Star, contentDescription = "Recompensas") },
            label = { Text("Recompensas") },
            selected = currentScreen == Screen.RecompensaList,
            onClick = { navController.navigate(Screen.RecompensaList) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") },
            selected = currentScreen == Screen.Padre,
            onClick = { }
        )
    }
}