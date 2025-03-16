package edu.ucne.doers.presentation.recompensa

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import edu.ucne.doers.data.local.model.ImageUtils
import java.io.File

@Composable
fun RecompensaScreen(
    viewModel: RecompensaViewModel = hiltViewModel(),
    recompensaId: Int,
    goRecompensasList: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            bitmap?.let { bmp ->
                val byteArray = ImageUtils.bitmapToByteArray(bmp)
                viewModel.savePhotoFromUri(context, it)
            }
        }
    }

    RecompensaBodyScreen(
        recompensaId,
        viewModel,
        uiState,
        goRecompensasList,
        onPickImage = { imagePickerLauncher.launch("image/*") },
        context
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecompensaBodyScreen(
    recompensaId: Int,
    viewModel: RecompensaViewModel,
    uiState: RecompensaUiState,
    goRecompensasList: () -> Unit,
    onPickImage: () -> Unit,
    context: Context
) {
    LaunchedEffect(recompensaId) {
        if (recompensaId > 0) {
            viewModel.find(recompensaId)
        } else {
            viewModel.new()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (recompensaId == 0) "Nueva Recompensa" else "Editar Recompensa") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    if (uiState.imagenURL.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(File(uiState.imagenURL)),
                            contentDescription = "Preview de la imagen",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .padding(bottom = 8.dp)
                        )
                    }
                    Button(onClick = onPickImage) {
                        Text("Seleccionar Imagen")
                    }
                    Spacer(modifier = Modifier.padding(4.dp))

                    OutlinedTextField(
                        label = { Text(text = "Descripción") },
                        value = uiState.descripcion,
                        onValueChange = viewModel::onDescripcionChange,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.padding(4.dp))

                    OutlinedTextField(
                        label = { Text(text = "Puntos Necesarios") },
                        value = uiState.puntosNecesarios.toString(),
                        onValueChange = {
                            viewModel.onPuntosNecesariosChange(
                                it.toIntOrNull() ?: 0
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.padding(4.dp))

                    uiState.errorMessage?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(onClick = {
                            if (recompensaId > 0) {
                                viewModel.delete(uiState.toEntity())
                                goRecompensasList()
                            } else {
                                viewModel.new()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = if (recompensaId > 0) "Borrar" else "Limpiar"
                            )
                            Text(text = if (recompensaId > 0) "Borrar" else "Limpiar")
                        }
                        OutlinedButton(
                            onClick = {
                                viewModel.save()
                                goRecompensasList()
                            }) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Guardar"
                            )
                            Text(text = "Guardar")
                        }
                    }
                }
            }
        }
    }
} 