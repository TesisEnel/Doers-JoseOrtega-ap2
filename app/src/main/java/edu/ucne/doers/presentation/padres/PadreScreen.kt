package edu.ucne.doers.presentation.padres

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import edu.ucne.doers.presentation.navigation.Screen

@Composable
fun PadreScreen(
    viewModel: PadreViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    Log.d("PadreScreen", "Renderizando PadreScreen - nombre: ${uiState.nombre}, fotoPerfil: ${uiState.fotoPerfil}, isLoading: ${uiState.isLoading}")

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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF0F2F5))
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!uiState.fotoPerfil.isNullOrEmpty()) {
                        AsyncImage(
                            model = uiState.fotoPerfil,
                            contentDescription = "Foto de perfil del padre",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                                .padding(bottom = 16.dp),
                            placeholder = painterResource(android.R.drawable.ic_dialog_info),
                            error = painterResource(android.R.drawable.ic_dialog_alert)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.nombre?.firstOrNull()?.toString() ?: "P",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Bienvenido, ${uiState.nombre ?: "Padre"}",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        ),
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (!uiState.email.isNullOrEmpty()) {
                        Text(
                            text = "Email: ${uiState.email}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    if (!uiState.codigoSala.isNullOrEmpty()) {
                        Text(
                            text = "Código de Sala: ${uiState.codigoSala}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "¡Estás listo para comenzar!",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            viewModel.signOut()
                            navController.navigate(Screen.Home) {
                                popUpTo(Screen.Padre) { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Cerrar Sesión",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
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
            onClick = {  }
        )
    }
}