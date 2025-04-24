package edu.ucne.doers.presentation.padres.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import edu.ucne.doers.presentation.padres.PadreUiState

@Composable
fun CerrarSesionDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    padreUiState: PadreUiState
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(20.dp)),
            icon = {
                if (!padreUiState.fotoPerfil.isNullOrEmpty()) {
                    AsyncImage(
                        model = padreUiState.fotoPerfil,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD3E5FA))
                            .border(2.dp, Color(0xFF4A90E2), CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFA000))
                            .border(2.dp, Color(0xFF4A90E2), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = padreUiState.nombre?.firstOrNull()?.toString() ?: "P",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            },
            title = {
                Text(
                    text = "¿Cerrar sesión?",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color(0xFF4A90E2)
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "Tu sesión se cerrará y deberás iniciar nuevamente para acceder.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        color = Color(0xFF666666)
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A90E2),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Sí, cerrar sesión",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Cancelar",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFFA000),
                        fontSize = 16.sp
                    )
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            properties = DialogProperties(dismissOnClickOutside = true)
        )
    }
}