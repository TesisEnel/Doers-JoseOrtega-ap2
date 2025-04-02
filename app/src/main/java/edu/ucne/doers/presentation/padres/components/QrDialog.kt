package edu.ucne.doers.presentation.padres.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

@Composable
fun QrDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    codigoSala: String?
) {
    if (showDialog && codigoSala != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Código QR de la Sala") },
            text = {
                Box(modifier = Modifier.fillMaxWidth().height(250.dp), contentAlignment = Alignment.Center) {
                    val qrBitmap = remember { generateQRCode(codigoSala, 200, 200) }
                    qrBitmap?.let {
                        Image(bitmap = it.asImageBitmap(), contentDescription = "Código QR", modifier = Modifier.size(200.dp))
                    } ?: Text("Error generando QR")
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cerrar")
                }
            }
        )
    }
}

fun generateQRCode(text: String, width: Int, height: Int): Bitmap? {
    return try {
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(
            text,
            BarcodeFormat.QR_CODE,
            width,
            height
        )
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(
                    x,
                    y,
                    if (bitMatrix[x, y]) Color.Black.hashCode() else Color.White.hashCode()
                )
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}