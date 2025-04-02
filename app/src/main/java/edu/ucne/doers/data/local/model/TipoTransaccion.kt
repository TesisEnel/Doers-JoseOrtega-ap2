package edu.ucne.doers.data.local.model

import kotlinx.serialization.Serializable

@Serializable
enum class TipoTransaccion(val nombreMostrable: String) {
    RECIBIDO("Recibido"),
    CONSUMIDO("Consumido")
}