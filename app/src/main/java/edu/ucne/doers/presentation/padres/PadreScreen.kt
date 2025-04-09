package edu.ucne.doers.presentation.padres

import android.content.Intent
import android.util.Log
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    onNavigateToPerfil: () -> Unit,
    onSignOut: () -> Unit
) {
    val padreUiState by padreViewModel.uiState.collectAsState()

    val hijos by padreViewModel.hijos.collectAsState()
    val tareasHijo by padreViewModel.tareasHijo.collectAsState()
    val recompensasMap by padreViewModel.recompensasPendientesMap.collectAsState()
    val hijoUiState by hijoViewModel.uiState.collectAsState()

    var showDialogAgregarPuntos by remember { mutableStateOf(false) }
    var showDialogEliminarHijo by remember { mutableStateOf(false) }
    var showDialogCerrarSesion by remember { mutableStateOf(false) }
    var showQrDialog by remember { mutableStateOf(false) }

    var selectedHijo by remember { mutableStateOf<HijoEntity?>(null) }
    var puntosAgregar by remember { mutableStateOf("") }


    val context = LocalContext.current
    val toastMessage by padreViewModel.toastMessage.collectAsState()
    val hijoToastMessage by hijoViewModel.uiState.collectAsState()

    LaunchedEffect(toastMessage, hijoUiState.successMessage, hijoUiState.errorMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            padreViewModel.clearToast()
        }
        hijoUiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            padreViewModel.getHijosByPadre(padreUiState.padreId ?: "") // Actualizar lista de hijos
            hijoViewModel.clearMessages() // Limpiar mensaje
        }
        hijoUiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            hijoViewModel.clearMessages() // Limpiar mensaje
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
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
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
                        Box(modifier = Modifier.size(60.dp), contentAlignment = Alignment.Center) {
                            if (!padreUiState.fotoPerfil.isNullOrEmpty()) {
                                AsyncImage(
                                    model = padreUiState.fotoPerfil,
                                    contentDescription = "Foto de perfil del padre",
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = padreUiState.nombre?.firstOrNull()?.toString()
                                            ?: "P",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = padreUiState.nombre ?: "Padre",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            padreUiState.email?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Text(
                                    text = "Padre 👨‍👩‍👧‍👦",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                        IconButton(
                            onClick = { showDialogCerrarSesion = true },
                            modifier = Modifier
                                .size(40.dp)
                                .padding(end = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Cerrar sesión",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Código de Sala",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = padreUiState.codigoSala ?: "N/A",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp,
                                        color = MaterialTheme.colorScheme.primary
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
                                    Toast.makeText(context, "Código copiado", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = "Copiar Código",
                                    tint = MaterialTheme.colorScheme.primary
                                )
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
                                    contentDescription = "Compartir Código",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(
                                onClick = { showQrDialog = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.QrCode,
                                    contentDescription = "Ver QR",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    ResumenPadreCards(
                        hijos = hijos,
                        tareasHijo = tareasHijo,
                        recompensasPendientesMap = recompensasMap,
                        tareas = padreViewModel.tareas.collectAsState().value,
                        onCardClick = {tipo -> Log.d("PadreScreen", "Card $tipo clickeado")},
                        onValidarTarea = { tarea ->
                            padreViewModel.validarTarea(tarea)
                        },
                        onNoValidarTarea = { tarea ->
                            padreViewModel.rechazarTarea(tarea)
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Hijos Registrados",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally),
                                textAlign = TextAlign.Center
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, bottom = 8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(onClick = {
                                    padreUiState.padreId?.let { hijoViewModel.getHijosByPadre(it)}
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Refresh,
                                        contentDescription = "Recargar Hijos",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Nombre",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Start
                                )
                                Text(
                                    text = "Puntos",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Agregar",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End
                                )
                                Text(
                                    text = "",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            HorizontalDivider()
                            hijos.forEach { hijo ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = hijo.nombre.ifEmpty { "Desconocido" },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Start
                                    )
                                    Text(
                                        text = "${hijo.saldoActual}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center
                                    )
                                    IconButton(
                                        onClick = {
                                            selectedHijo = hijo
                                            showDialogAgregarPuntos = true
                                        },
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Add,
                                            contentDescription = "Agregar Puntos",
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            selectedHijo = hijo
                                            showDialogEliminarHijo = true
                                        },
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = "Eliminar Hijo",
                                            tint = MaterialTheme.colorScheme.error
                                        )
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

    CerrarSesionDialog(
        showDialog = showDialogCerrarSesion,
        onDismiss = { showDialogCerrarSesion = false },
        onConfirm = {
            padreViewModel.signOut()
            onSignOut()
        },
        padreUiState = padreUiState
    )

    QrDialog(
        showDialog = showQrDialog,
        onDismiss = { showQrDialog = false },
        codigoSala = padreUiState.codigoSala
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
}