package edu.ucne.doers.data.local.model

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

object ImageUtils {
    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return outputStream.toByteArray()
    }
}
/*
object ImageUtils {
    fun bitmapToByteArray(context: Context, uri: Uri): ByteArray {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return outputStream.toByteArray()
    }
}*/
