package edu.ucne.doers.presentation.recompensa.hijo

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.doers.R
import edu.ucne.doers.data.local.entity.RecompensaEntity
import edu.ucne.doers.presentation.hijos.HijoViewModel
import edu.ucne.doers.presentation.navigation.Screen
import edu.ucne.doers.presentation.recompensa.comp.HijoNavBar
import edu.ucne.doers.presentation.recompensa.components.CanjeoRecompensaBottomSheet
import edu.ucne.doers.presentation.recompensa.components.ModalResumenCanjeo
import edu.ucne.doers.presentation.recompensa.components.ModalSaldoInsuficiente
import edu.ucne.doers.presentation.recompensa.components.RecompensaCardHijo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecompensasHijoScreen(
    viewModel: HijoViewModel = hiltViewModel(),
    onNavigateToTareas: () -> Unit,
    onNavigateToPerfil: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cantidades by remember { derivedStateOf { viewModel.cantidades } }
    val context = LocalContext.current

    val azulCielo = Color(0xFF4A90E2)
    val amarilloMoneda = Color(0xFFFFA000)

    var showBottomSheet by remember { mutableStateOf(false) }
    var showBottomSheetResumen by remember { mutableStateOf(false) }
    var selectedRecompensa by remember { mutableStateOf<RecompensaEntity?>(null) }
    var showSaldoInsuficienteDialog by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    val totalPuntos by remember(cantidades) { derivedStateOf { viewModel.getTotalPuntos() } }

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        uiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
    }

    fun cerrarModalRecompensa() {
        if (selectedRecompensa != null) {
            viewModel.clearCantidadTemporal(selectedRecompensa!!.recompensaId.toString())
        }
        selectedRecompensa = null
        showBottomSheet = false
    }

    Scaffold(
        bottomBar = {
            HijoNavBar(
                currentScreen = Screen.RecompensaHijo,
                onTareasClick = onNavigateToTareas,
                onRecompensasClick = {},
                onPerfilClick = onNavigateToPerfil
            )
        },
        containerColor = Color(0xFFF9F9F9)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Fondo azul
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(amarilloMoneda),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier.padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = "Icono Tienda",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tienda de Recompensas",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Tarjeta blanca principal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp)
                    .offset(y = 140.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { showBottomSheetResumen = true }
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingCart,
                                contentDescription = "Resumen de Canjeos",
                                tint = azulCielo,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Carrito",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = azulCielo
                            )
                        }

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                            border = BorderStroke(1.dp, Color(0xFFFFD700)),
                            modifier = Modifier.width(160.dp)
                        ) {
                            TextField(
                                value = TextFieldValue("${uiState.saldoEfectivo}"),
                                onValueChange = {},
                                readOnly = true,
                                leadingIcon = {
                                    Image(
                                        painter = painterResource(id = R.drawable.dollar),
                                        contentDescription = "Saldo",
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp)),
                                colors = TextFieldDefaults.colors(
                                    disabledTextColor = amarilloMoneda,
                                    disabledContainerColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                ),
                                enabled = false,
                                textStyle = LocalTextStyle.current.copy(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    // Mostrar imagen y texto si no hay recompensas
                    if (uiState.listaRecompensasFiltradas.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.imagen_de_espera),
                                contentDescription = "No hay recompensas disponibles",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(250.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tu padre o tutor todavía no te ha creado recompensas",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(100.dp)) // Espacio para la card superpuesta
                    }
                }
            }

            // Card transparente superpuesta con scroll de recompensas
            if (uiState.listaRecompensasFiltradas.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 10.dp)
                        .offset(y = 220.dp)
                        .heightIn(min = 200.dp, max = 600.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(0.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                            .padding(16.dp)
                    ) {
                        Text(
                            "Recompensas Disponibles",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = azulCielo
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.listaRecompensasFiltradas, key = { it.recompensaId }) { recompensa ->
                                RecompensaCardHijo(
                                    recompensa = recompensa,
                                    onReclamar = {
                                        selectedRecompensa = recompensa
                                        viewModel.setCantidadTemporal(recompensa.recompensaId.toString(), 1)
                                        showBottomSheet = true
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Modal resumen de canjeo
            ModalResumenCanjeo(
                showSheet = showBottomSheetResumen,
                onDismiss = { showBottomSheetResumen = false },
                listaRecompensas = uiState.listaRecompensasFiltradas,
                cantidades = cantidades,
                totalPuntos = totalPuntos,
                uiState = uiState,
                onCantidadChange = viewModel::onCantidadChange,
                onCheckout = {
                    if (uiState.saldoEfectivo >= totalPuntos) {
                        uiState.listaRecompensasFiltradas.forEach {
                            val cantidad = cantidades[it.recompensaId.toString()] ?: 0
                            if (cantidad > 0) {
                                viewModel.reclamarRecompensa(it.recompensaId)
                            }
                        }
                        viewModel.clearCarrito()
                        showBottomSheetResumen = false
                    } else {
                        showSaldoInsuficienteDialog = true
                    }
                }
            )

            // Modal confirmar canjeo
            if (showBottomSheet && selectedRecompensa != null) {
                CanjeoRecompensaBottomSheet(
                    recompensa = selectedRecompensa!!,
                    cantidades = cantidades,
                    cantidadTemporal = viewModel.cantidadTemporal,
                    setCantidadTemporal = viewModel::setCantidadTemporal,
                    onCantidadChange = viewModel::onCantidadChange,
                    uiState = uiState,
                    onDismiss = { cerrarModalRecompensa() },
                    onConfirm = { cerrarModalRecompensa() },
                    onShowSaldoInsuficiente = { showSaldoInsuficienteDialog = true }
                )
            }

            // Modal saldo insuficiente
            ModalSaldoInsuficiente(
                showDialog = showSaldoInsuficienteDialog,
                onDismiss = { showSaldoInsuficienteDialog = false },
                saldoActual = uiState.saldoEfectivo,
                saldoNecesario = if (showBottomSheet && selectedRecompensa != null) {
                    val cantidad = viewModel.cantidadTemporal[selectedRecompensa!!.recompensaId.toString()] ?: 1
                    selectedRecompensa!!.puntosNecesarios * cantidad
                } else {
                    totalPuntos
                }
            )
        }
    }
}