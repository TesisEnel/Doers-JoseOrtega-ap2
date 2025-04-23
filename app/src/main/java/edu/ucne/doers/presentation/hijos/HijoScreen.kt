package edu.ucne.doers.presentation.hijos

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import edu.ucne.doers.R
import edu.ucne.doers.data.local.entity.TransaccionHijo
import edu.ucne.doers.data.local.model.TipoTransaccion
import edu.ucne.doers.presentation.navigation.Screen
import edu.ucne.doers.presentation.padres.PadreViewModel
import edu.ucne.doers.presentation.recompensa.comp.HijoNavBar
import java.text.SimpleDateFormat
import java.util.Date


@SuppressLint("SimpleDateFormat")
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
    val transacciones by hijoViewModel.transacciones.collectAsState()
    var selectedTransaccion by remember { mutableStateOf<TransaccionHijo?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        hijoViewModel.getTransacciones()
    }

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
                                model = R.drawable.personaje_1,
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
                    Spacer(modifier = Modifier.height(32.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Últimas Transacciones",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colors.primary
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = {
                                    hijoViewModel.getTransacciones()
                                    Toast.makeText(
                                        context,
                                        "Transacciones actualizadas",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Recargar transacciones",
                                        tint = colors.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            if (transacciones.isEmpty()) {
                                Text(
                                    text = "No hay transacciones recientes",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.onSurfaceVariant,
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                transacciones.take(5).forEach { transaccion ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedTransaccion = transaccion
                                                showDialog = true
                                            }
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Visibility,
                                            contentDescription = "Ver detalles",
                                            modifier = Modifier.size(16.dp),
                                            tint = colors.primary
                                        )
                                        Text(
                                            text = "${transaccion.tipo.nombreMostrable}: ${transaccion.monto} puntos",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (transaccion.tipo == TipoTransaccion.RECIBIDO) colors.primary else colors.error
                                        )
                                        Text(
                                            text = SimpleDateFormat("dd/MM/yyyy hh:mm a").format(
                                                transaccion.fecha
                                            ),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = colors.onSurfaceVariant,
                                            overflow = TextOverflow.Ellipsis,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                    if (showDialog && selectedTransaccion != null) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            confirmButton = {
                                TextButton(onClick = { showDialog = false }) {
                                    Text("Cerrar")
                                }
                            },
                            title = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Detalles de Transacción",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = colors.primary
                                        ),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            },
                            text = {
                                Column(modifier = Modifier.padding(top = 8.dp)) {
                                    Text(
                                        buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append("Tipo: ")
                                            }
                                            append(selectedTransaccion?.tipo?.nombreMostrable ?: "")
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = colors.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(12.dp)) // Mayor separación entre elementos
                                    Text(
                                        buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append("Monto: ")
                                            }
                                            append("${selectedTransaccion?.monto ?: 0} puntos")
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = colors.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(12.dp)) // Mayor separación entre elementos
                                    Text(
                                        buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append("Fecha: ")
                                            }
                                            append(
                                                SimpleDateFormat("dd/MM/yyyy hh:mm a").format(
                                                    selectedTransaccion?.fecha ?: Date()
                                                )
                                            )
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = colors.onSurface
                                    )
                                    selectedTransaccion?.descripcion?.let {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Descripción:",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = colors.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = it,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = colors.onSurfaceVariant
                                        )
                                    }
                                }
                            },
                            shape = MaterialTheme.shapes.large,
                            containerColor = colors.surface,
                            tonalElevation = 8.dp
                        )
                    }
                }
            }
        }
    }
}