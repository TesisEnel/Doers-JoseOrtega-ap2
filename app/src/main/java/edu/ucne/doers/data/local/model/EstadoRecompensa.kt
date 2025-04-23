package edu.ucne.doers.data.local.model

import androidx.compose.ui.graphics.Color
import com.squareup.moshi.Json

enum class EstadoRecompensa {
    @Json(name = "PENDIENTE") PENDIENTE,
    @Json(name = "COMPLETADA") COMPLETADA,
    @Json(name = "CANCELADA") CANCELADA;

    fun nombreMostrable(): String = when (this) {
        PENDIENTE -> "Pendiente"
        COMPLETADA -> "Completada"
        CANCELADA -> "Cancelada"
    }

    fun color(): Color = when (this) {
        PENDIENTE -> Color(0xFFFFA000)
        COMPLETADA -> Color(0xFF388E3C)
        CANCELADA -> Color(0xFFD32F2F)
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