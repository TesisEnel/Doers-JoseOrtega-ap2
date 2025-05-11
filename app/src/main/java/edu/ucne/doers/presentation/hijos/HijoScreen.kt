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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
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
import androidx.compose.ui.graphics.Color
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
    val transacciones by hijoViewModel.transacciones.collectAsState()
    var selectedTransaccion by remember { mutableStateOf<TransaccionHijo?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val colors = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        hijoViewModel.getTransacciones()
    }

    if (hijoUiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(Color(0xFFF8F9FB))
                    .padding(innerPadding)
            ) {
                // Fondo azul redondeado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFA000))
                        .height(200.dp)
                )

                // Tarjeta de Perfil
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .offset(y = (-100).dp),
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
                        AsyncImage(
                            model = R.drawable.personaje_1,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD3E5FA))
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = hijoUiState.nombre ?: "Hijo",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFFFFA000),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Cuenta de Hijo 👦👧",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
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

                // Tarjeta de Código de Sala
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .offset(y = (-80).dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
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
                                            color = Color(0xFF4A90E2),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    ) {
                                        append(padreUiState.nombre ?: "Padre")
                                    }
                                },
                                fontSize = 15.sp
                            )
                            Text(
                                text = padreUiState.codigoSala ?: "N/A",
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = Color(0xFFFFA000)
                            )
                        }
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(padreUiState.codigoSala ?: ""))
                            Toast.makeText(context, "Código copiado", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null)
                        }
                    }
                }

                // Tarjeta de Transacciones
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .offset(y = (-60).dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Últimas Transacciones",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFFFFA000)
                            )
                            IconButton(onClick = {
                                hijoViewModel.getTransacciones()
                                Toast.makeText(context, "Transacciones actualizadas", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFFFFA000))
                            }
                        }

                        if (transacciones.isEmpty()) {
                            Text(
                                text = "No hay transacciones recientes",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                                color = Color.Gray
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
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Icon(Icons.Default.Visibility, contentDescription = null, tint = Color(0xFFFFA000))
                                    Text(
                                        text = "${transaccion.tipo.nombreMostrable}: ${transaccion.monto} 🪙",
                                        color = if (transaccion.tipo == TipoTransaccion.RECIBIDO) Color(0xFF388E3C) else Color.Red,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = SimpleDateFormat("dd/MM/yyyy hh:mm a").format(transaccion.fecha),
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }

                // Modal
                if (showDialog && selectedTransaccion != null) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        confirmButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Cerrar")
                            }
                        },
                        title = {
                            Text(
                                text = "Detalles de Transacción",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF4A90E2),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        },
                        text = {
                            Column {
                                Text(buildAnnotatedString {
                                    append("Tipo: ")
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(selectedTransaccion?.tipo?.nombreMostrable ?: "")
                                    }
                                })
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(buildAnnotatedString {
                                    append("Monto: ")
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("${selectedTransaccion?.monto} puntos")
                                    }
                                })
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(buildAnnotatedString {
                                    append("Fecha: ")
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(SimpleDateFormat("dd/MM/yyyy hh:mm a").format(selectedTransaccion?.fecha ?: Date()))
                                    }
                                })
                                selectedTransaccion?.descripcion?.let {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Descripción:", fontWeight = FontWeight.Bold)
                                    Text(it)
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        containerColor = Color.White
                    )
                }
            }
        }
    }
}