package edu.ucne.doers.presentation.recompensa.padre

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.doers.presentation.recompensa.RecompensaUiState
import edu.ucne.doers.presentation.recompensa.RecompensaViewModel
import edu.ucne.doers.presentation.recompensa.components.CreateRecompensaForm

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
            bitmap?.let {
                viewModel.savePhotoFromUri(context, uri)
            }
        }
    }

    RecompensaBodyScreen(
        viewModel = viewModel,
        uiState = uiState,
        recompensaId = recompensaId,
        goBack = goRecompensasList,
        onPickImage = { imagePickerLauncher.launch("image/*") }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecompensaBodyScreen(
    viewModel: RecompensaViewModel,
    uiState: RecompensaUiState,
    recompensaId: Int,
    goBack: () -> Unit,
    onPickImage: () -> Unit
) {
    val azulMar = Color(0xFF1976D2)

    /*LaunchedEffect(recompensaId) {
        if (recompensaId > 0) {
            viewModel.find(recompensaId)
        } else {
            viewModel.new()
        }
    }*/

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Doers",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            modifier = Modifier.padding(end = 50.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = azulMar)
            )
        }
    ) { padding ->
        CreateRecompensaForm(
            modifier = Modifier.padding(padding),
            viewModel = viewModel,
            uiState = uiState,
            goBack = goBack,
            recompensaId = recompensaId,
            onPickImage = onPickImage
        )
    }
}