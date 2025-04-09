package edu.ucne.doers.presentation.hijos

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.ucne.doers.presentation.componentes.QrScannerDialog
import kotlinx.coroutines.launch

@Composable
fun HijoLoginScreen(
    onChildLoggedIn: () -> Unit,
    viewModel: HijoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var childName by remember { mutableStateOf("") }
    var roomCode by remember { mutableStateOf("") }
    var showQrScanner by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showQrScanner = true
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Se requiere permiso de cámara para escanear QR")
            }
        }
    }

    LaunchedEffect(uiState.signInError) {
        uiState.signInError?.let { errorMsg ->
            snackbarHostState.showSnackbar(errorMsg)
        }
    }

    LaunchedEffect(uiState.isSignInSuccessful, uiState.isLoading) {
        if (uiState.isSignInSuccessful && !uiState.isLoading) {
            onChildLoggedIn()
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            containerColor = Color(0xFFFDFDFD),
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¡Bienvenido a Doers!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Icon(
                        imageVector = Icons.Filled.ChildCare,
                        contentDescription = "Icono de Niño",
                        modifier = Modifier.size(100.dp),
                        tint = Color(0xFF42A5F5)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        value = childName,
                        onValueChange = { childName = it },
                        label = { Text(
                            "Ingresa tu nombre",
                            color = Color(0xFF212121)
                        ) },
                        textStyle = TextStyle(color = Color.Black),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = roomCode,
                            onValueChange = { roomCode = it },
                            label = { Text(
                                "Código de la sala",
                                color = Color(0xFF212121)
                            ) },
                            textStyle = TextStyle(color = Color.Black),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                permissionLauncher.launch(android.Manifest.permission.CAMERA)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.QrCodeScanner,
                                contentDescription = "Escanear QR",
                                tint = Color(0xFF4CAF50)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.loginChild(childName, roomCode) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Unirse a la sala", fontSize = 20.sp)
                        }
                    }
                }

                if (showQrScanner) {
                    QrScannerDialog(
                        onQrCodeScanned = { code ->
                            roomCode = code
                            showQrScanner = false
                        },
                        onDismiss = { showQrScanner = false }
                    )
                }
            }
        }
    }
}