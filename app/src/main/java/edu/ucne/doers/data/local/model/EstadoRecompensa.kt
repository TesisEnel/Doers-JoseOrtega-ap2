package edu.ucne.doers.data.local.model

import androidx.compose.ui.graphics.Color
import com.squareup.moshi.Json


enum class EstadoRecompensa {
    @Json(name = "DISPONIBLE") DISPONIBLE,
    @Json(name = "NO_DISPONIBLE") NO_DISPONIBLE;

    fun nombreMostrable() = when(this) {
        DISPONIBLE -> "Disponible"
        NO_DISPONIBLE -> "No Disponible"
    }

    fun color(): Color = when(this) {
        DISPONIBLE -> Color(0xFF388E3C)
        NO_DISPONIBLE -> Color.Red
    }
}

enum class CondicionRecompensa {
    @Json(name = "ACTIVA") ACTIVA,
    @Json(name = "INACTIVA") INACTIVA;

    fun nombreMostrable(): String = when (this) {
        ACTIVA -> "Activa"
        INACTIVA -> "Inactiva"
    }
}

enum class EstadoCanjeo {
    @Json(name = "PENDIENTE_VERIFICACION") PENDIENTE_VERIFICACION,
    @Json(name = "APROBADO") APROBADO,
    @Json(name = "RECHAZADO") RECHAZADO;

    fun nombreMostrable(): String = when (this) {
        PENDIENTE_VERIFICACION -> "Pendiente de verificación"
        APROBADO -> "Aprobado"
        RECHAZADO -> "Rechazado"
    }
}