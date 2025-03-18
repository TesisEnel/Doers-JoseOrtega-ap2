package edu.ucne.doers.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object Home : Screen()
    @Serializable
    data object Padre : Screen()
    @Serializable
    data object RecompensaList : Screen()
    @Serializable
    data class Recompensa(val recompensaId: Int) : Screen()
    @Serializable
    data object PantallaTareas : Screen()
    @Serializable
    data class  CrearTarea(val tareaId: Int) : Screen()
}