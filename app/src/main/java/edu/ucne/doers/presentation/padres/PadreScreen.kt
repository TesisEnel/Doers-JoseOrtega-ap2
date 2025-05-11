package edu.ucne.doers.presentation.padres

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import edu.ucne.doers.data.local.entity.HijoEntity
import edu.ucne.doers.presentation.componentes.PadreNavBar
import edu.ucne.doers.presentation.hijos.HijoViewModel
import edu.ucne.doers.presentation.navigation.Screen
import edu.ucne.doers.presentation.padres.components.AgregarPuntosDialog
import edu.ucne.doers.presentation.padres.components.CerrarSesionDialog
import edu.ucne.doers.presentation.padres.components.EliminarHijoDialog
import edu.ucne.doers.presentation.padres.components.QrDialog
import edu.ucne.doers.presentation.padres.tareas.ResumenPadreCards

@Composable
fun PadreScreen(
    padreViewModel: PadreViewModel,
    hijoViewModel: HijoViewModel,
    onNavigateToTareas: () -> Unit,
    onNavigateToRecompensas: () -> Unit,
    goToTareasVerificar: () -> Unit,
    goToToRecompensasVerificar: () -> Unit,
    onSignOut: () -> Unit
) {
    val padreUiState by padreViewModel.uiState.collectAsState()
    val hijos by padreViewModel.hijos.collectAsState()
    val tareasHijo by padreViewModel.tareasHijo.collectAsState()
    val canjeos by padreViewModel.canjeoHijo.collectAsState()
    val hijoUiState by hijoViewModel.uiState.collectAsState()

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var showDialogAgregarPuntos by remember { mutableStateOf(false) }
    var showDialogEliminarHijo by remember { mutableStateOf(false) }
    var showDialogCerrarSesion by remember { mutableStateOf(false) }
    var showQrDialog by remember { mutableStateOf(false) }
    var selectedHijo by remember { mutableStateOf<HijoEntity?>(null) }
    var puntosAgregar by remember { mutableStateOf("") }

    val toastMessage by padreViewModel.toastMessage.collectAsState()

    LaunchedEffect(toastMessage, hijoUiState.successMessage, hijoUiState.errorMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            padreViewModel.clearToast()
        }
        hijoUiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            padreViewModel.getHijosByPadre(padreUiState.padreId ?: "")
            hijoViewModel.clearMessages()
        }
        hijoUiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            hijoViewModel.clearMessages()
        }
    }

    if (padreUiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            bottomBar = {
                PadreNavBar(
                    currentScreen = Screen.Padre,
                    onTareasClick = onNavigateToTareas,
                    onRecompensasClick = onNavigateToRecompensas,
                    onPerfilClick = {}
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(Color(0xFFF8F9FB))
                    .padding(innerPadding)
            ) {
                // Fondo azul superior
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Color(0xFF4A90E2))
                )

                // Tarjeta Perfil
                Card(
                    modifier = Modifier
                        .offset(y = (-100).dp)
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(onClick = { showDialogCerrarSesion = true }) {
                                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión")
                            }
                        }
                        if (!padreUiState.fotoPerfil.isNullOrEmpty()) {
                            AsyncImage(
                                model = padreUiState.fotoPerfil,
                                contentDescription = "Foto de perfil",
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD3E5FA))
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFA000)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = padreUiState.nombre?.firstOrNull()?.toString() ?: "P",
                                    fontSize = 28.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = padreUiState.nombre ?: "Padre",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF4A90E2)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Cuenta de Padre 👨‍👩‍👧‍👦",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        // Iconos ilustrativos
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            repeat(4) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = Color(0xFFFFA000),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }

                // Tarjeta Código Sala
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .offset(y = (-80).dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            color = Color.Gray,
                                            fontWeight = FontWeight.Bold
                                        )
                                    ) {
                                        append("Código de Sala ")
                                    }
                                    withStyle(
                                        style = SpanStyle(
                                            color = Color(0xFFFFA000),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 22.sp
                                        )
                                    ) {
                                        append(padreUiState.codigoSala ?: "N/A")
                                    }
                                },
                                fontSize = 16.sp
                            )
                        }
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(padreUiState.codigoSala ?: ""))
                            Toast.makeText(context, "Código copiado", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null)
                        }
                        IconButton(
                            onClick = {
                                val mensaje = """
                                    🎯 ¡${padreUiState.nombre} te invita a unirte a su sala en Doers! 👨‍👩‍👧‍👦
                                    Ha creado una sala en Doers para motivarnos a completar tareas y ganar recompensas. 🎁
                                    🔑 Código de la sala: ${padreUiState.codigoSala}
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
                                contentDescription = "Compartir Código"
                            )
                        }
                        IconButton(onClick = { showQrDialog = true }) {
                            Icon(Icons.Default.QrCode, contentDescription = null)
                        }
                    }
                }

                // Tarjeta Resumen de Actividades
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .offset(y = (-60).dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {

                        Spacer(modifier = Modifier.height(12.dp))

                        ResumenPadreCards(
                            tareasHijo = tareasHijo,
                            canjeos = canjeos,
                            goToTareasVerificar = goToTareasVerificar,
                            goToRecompensasVerificar = goToToRecompensasVerificar
                        )
                    }
                }

                // Tarjeta Hijos
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Hijos Registrados",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A90E2),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            IconButton(onClick = {
                                padreUiState.padreId?.let { hijoViewModel.getHijosByPadre(it) }
                            }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = Color(0xFF4A90E2))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        // Encabezados
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Nombre",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Puntos",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Acciones",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                        // Lista de hijos
                        hijos.forEach { hijo ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = hijo.nombre,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "${hijo.saldoActual} pts",
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center
                                )
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    IconButton(onClick = {
                                        selectedHijo = hijo
                                        showDialogAgregarPuntos = true
                                    }) {
                                        Icon(Icons.Default.Add, contentDescription = null)
                                    }
                                    IconButton(onClick = {
                                        selectedHijo = hijo
                                        showDialogEliminarHijo = true
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    AgregarPuntosDialog(
        showDialog = showDialogAgregarPuntos,
        onDismiss = { showDialogAgregarPuntos = false },
        onAgregar = { puntos ->
            hijoViewModel.agregarPuntos(selectedHijo!!, puntos)
            showDialogAgregarPuntos = false
            puntosAgregar = ""
        },
        selectedHijo = selectedHijo,
        puntosAgregar = puntosAgregar,
        onPuntosChange = { puntosAgregar = it }
    )

    EliminarHijoDialog(
        showDialog = showDialogEliminarHijo,
        onDismiss = { showDialogEliminarHijo = false },
        onConfirm = {
            selectedHijo?.let { hijoViewModel.eliminarHijo(it) }
            showDialogEliminarHijo = false
        },
        selectedHijo = selectedHijo
    )

    QrDialog(
        showDialog = showQrDialog,
        onDismiss = { showQrDialog = false },
        codigoSala = padreUiState.codigoSala
    )

    CerrarSesionDialog(
        showDialog = showDialogCerrarSesion,
        onDismiss = { showDialogCerrarSesion = false },
        onConfirm = {
            padreViewModel.signOut()
            onSignOut()
        },
        padreUiState = padreUiState
    )
}