package edu.ucne.doers.data.local.model

import androidx.compose.ui.graphics.Color
import com.squareup.moshi.Json

enum class EstadoTarea {
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

enum class CondicionTarea {
    @Json(name = "ACTIVA") ACTIVA,
    @Json(name = "INACTIVA") INACTIVA;

    fun nombreMostrable(): String = when (this) {
        ACTIVA -> "Activa"
        INACTIVA -> "Inactiva"
    }
}

enum class PeriodicidadTarea {
    @Json(name = "DIARIA") DIARIA,
    @Json(name = "SEMANAL") SEMANAL,
    @Json(name = "UNICA") UNICA;

    fun nombreMostrable(): String = when (this) {
        DIARIA -> "Diaria"
        SEMANAL -> "Semanal"
        UNICA -> "Única"
    }
}

enum class EstadoTareaHijo {
    @Json(name = "PENDIENTE_VERIFICACION") PENDIENTE_VERIFICACION,
    @Json(name = "APROBADA") APROBADA,
    @Json(name = "RECHAZADA") RECHAZADA;

    fun nombreMostrable(): String = when (this) {
        PENDIENTE_VERIFICACION -> "Pendiente de verificación"
        APROBADA -> "Aprobada"
        RECHAZADA -> "Rechazada"
    }
}