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
    data object RecompensaHijo : Screen()
    @Serializable
    data class Recompensa(val recompensaId: Int) : Screen()
    @Serializable
    data object TareaList : Screen()
    @Serializable
    data class  Tarea(val tareaId: Int) : Screen()
    @Serializable
    data object TareaHijo : Screen()
    @Serializable
    data object HijoLogin : Screen()
    @Serializable
    data object Hijo : Screen()
    @Serializable
    data object ActividadesTarea : Screen()
    @Serializable
    data object ActividadesRecompensa : Screen()
}