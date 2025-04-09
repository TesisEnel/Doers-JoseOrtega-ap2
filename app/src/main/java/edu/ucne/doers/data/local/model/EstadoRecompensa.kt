package edu.ucne.doers.data.local.model

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import com.squareup.moshi.Json

@Serializable
enum class EstadoRecompensa {
    @Json(name = "DISPONIBLE") DISPONIBLE,
    @Json(name = "CADUCADA") CADUCADA,
    @Json(name = "AGOTADA") AGOTADA,
    @Json(name = "PENDIENTE") PENDIENTE,
    @Json(name = "RECLAMADA") RECLAMADA;

    fun nombreMostrable(): String = when (this) {
        DISPONIBLE -> "Disponible"
        CADUCADA -> "Caducada"
        AGOTADA -> "Agotada"
        PENDIENTE -> "Pendiente"
        RECLAMADA -> "Reclamada"
    }

    fun color(): Color = when (this) {
        DISPONIBLE -> Color(0xFF4CAF50)
        CADUCADA -> Color(0xFF9E9E9E)
        AGOTADA -> Color(0xFFF44336)
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