package edu.ucne.doers.presentation.recompensa.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import edu.ucne.doers.presentation.recompensa.RecompensaUiState
import edu.ucne.doers.presentation.recompensa.RecompensaViewModel
import java.io.File

@Composable
fun CreateRecompensaForm(
    modifier: Modifier,
    viewModel: RecompensaViewModel,
    uiState: RecompensaUiState,
    goBack: () -> Unit,
    recompensaId: Int,
    onPickImage: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = if (recompensaId > 0) "Modificar Recompensa" else "Crear Nueva Recompensa",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = uiState.descripcion,
            onValueChange = viewModel::onDescripcionChange,
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.errorMessage?.contains("descripción") == true,
            supportingText = {
                if (uiState.errorMessage?.contains("descripción") == true)
                    Text(uiState.errorMessage ?: "", color = Color.Red)
            }
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.puntosNecesarios.toString(),
            onValueChange = { viewModel.onPuntosNecesariosChange(it.toIntOrNull() ?: 0) },
            label = { Text("Puntos necesarios") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            isError = uiState.errorMessage?.contains("puntos") == true,
            supportingText = {
                if (uiState.errorMessage?.contains("puntos") == true)
                    Text(uiState.errorMessage ?: "", color = Color.Red)
            }
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Imagen de la Recompensa",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.imagenURL.isNotEmpty()) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = rememberAsyncImagePainter(File(uiState.imagenURL)),
                            contentDescription = "Vista previa de imagen",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Fit
                        )
                        IconButton(
                            onClick = { viewModel.clearImage() },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Color.White.copy(alpha = 0.7f), shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar imagen",
                                tint = Color.Red
                            )
                        }
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Sin imagen",
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onPickImage,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.UploadFile, contentDescription = "Subir imagen")
            Spacer(Modifier.width(8.dp))
            Text("Seleccionar Imagen")
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (recompensaId > 0) {
                Button(
                    onClick = {
                        viewModel.save()
                        goBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Modificar", tint = Color.White)
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Modificar", color = Color.White)
                }
            } else {
                Button(
                    onClick = { viewModel.new() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nuevo", tint = Color.White)
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Nuevo", color = Color.White)
                }
                Button(
                    onClick = {
                        viewModel.save()
                        goBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Guardar", tint = Color.White)
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Guardar", color = Color.White)
                }
            }
        }
    }
}
