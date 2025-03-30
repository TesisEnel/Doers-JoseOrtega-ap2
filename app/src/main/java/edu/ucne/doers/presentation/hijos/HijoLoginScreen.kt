package edu.ucne.doers.presentation.hijos

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.launch
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.ui.text.TextStyle

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

    LaunchedEffect(uiState.isSignInSuccessful) {
        if (uiState.isSignInSuccessful) {
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

@OptIn(ExperimentalGetImage::class)
@Composable
fun QrScannerDialog(
    onQrCodeScanned: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        text = {
            Column {
                Box(
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth()
                ) {
                    AndroidView(
                        factory = { ctx ->
                            PreviewView(ctx).apply {
                                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                            }
                        },
                        update = { previewView ->
                            cameraProviderFuture.addListener({
                                try {
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder()
                                        .build()
                                        .also {
                                            it.setSurfaceProvider(previewView.surfaceProvider)
                                        }

                                    val options = BarcodeScannerOptions.Builder()
                                        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                                        .build()

                                    val scanner = BarcodeScanning.getClient(options)

                                    val imageAnalysis = ImageAnalysis.Builder()
                                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                        .build()
                                        .also { analysis ->
                                            analysis.setAnalyzer(
                                                Executors.newSingleThreadExecutor()
                                            ) { imageProxy ->
                                                val mediaImage = imageProxy.image
                                                if (mediaImage != null) {
                                                    val image = InputImage.fromMediaImage(
                                                        mediaImage,
                                                        imageProxy.imageInfo.rotationDegrees
                                                    )
                                                    scanner.process(image)
                                                        .addOnSuccessListener { barcodes ->
                                                            barcodes.firstOrNull()?.rawValue?.let { code ->
                                                                Log.d("QRScanner", "Código detectado: $code")
                                                                onQrCodeScanned(code)
                                                            }
                                                        }
                                                        .addOnFailureListener { e ->
                                                            Log.e("QRScanner", "Error escaneando: ${e.message}")
                                                            coroutineScope.launch {
                                                                snackbarHostState.showSnackbar(
                                                                    "Error al escanear: ${e.message}"
                                                                )
                                                            }
                                                        }
                                                        .addOnCompleteListener {
                                                            imageProxy.close()
                                                        }
                                                } else {
                                                    imageProxy.close()
                                                }
                                            }
                                        }

                                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        cameraSelector,
                                        preview,
                                        imageAnalysis
                                    )
                                } catch (e: Exception) {
                                    Log.e("QRScanner", "Error iniciando cámara: ${e.message}")
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Error iniciando cámara: ${e.message}"
                                        )
                                    }
                                }
                            }, ContextCompat.getMainExecutor(context))
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    )
}