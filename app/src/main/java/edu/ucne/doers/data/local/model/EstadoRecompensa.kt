package edu.ucne.doers.data.local.model

import androidx.compose.ui.graphics.Color
import com.squareup.moshi.Json
import kotlinx.serialization.Serializable

@Serializable
enum class EstadoRecompensa {
    @Json(name = "DISPONIBLE") DISPONIBLE,
    @Json(name = "NO DISPONIBLE") NO_DISPONIBLE,
    @Json(name = "PENDIENTE") PENDIENTE,
    @Json(name = "RECLAMADA") RECLAMADA;

    fun nombreMostrable(): String = when (this) {
        DISPONIBLE -> "Disponible"
        NO_DISPONIBLE -> "No Disponible"
        PENDIENTE -> "Pendiente"
        RECLAMADA -> "Reclamada"
    }

    fun color(): Color = when (this) {
        DISPONIBLE -> Color(0xFF4CAF50)
        NO_DISPONIBLE -> Color(0xFFF44336)
        PENDIENTE -> Color(0xFFFFA000)
        RECLAMADA -> Color(0xFF2196F3)
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