package edu.ucne.doers.data.local.model

import kotlinx.serialization.Serializable

@Serializable
enum class EstadoRecompensa(val nombreMostrable: String) {
    DISPONIBLE("Disponible"),
    CADUCADA("Caducada"),
    AGOTADA("Agotada")
}