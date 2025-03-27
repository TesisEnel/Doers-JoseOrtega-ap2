package edu.ucne.doers.data.local.model

import androidx.compose.ui.graphics.Color

enum class EstadoTarea(val nombreMostrable: String, val color: Color) {
    PENDIENTE("Pendiente", Color(0xFFFFA000)),
    COMPLETADA("Completada", Color(0xFF388E3C)),
    CANCELADA("Cancelada", Color(0xFFD32F2F))
}

enum class CondicionTarea(val nombreMostrable: String) {
    ACTIVA("Activa"),
    INACTIVA("Inactiva")
}

enum class PeriodicidadTarea(val nombreMostrable: String) {
    DIARIA("Diaria"),
    SEMANAL("Semanal"),
    UNICA("Unica")
}

enum class EstadoTareaHijo(val nombreMostrable: String) {
    PENDIENTE_VERIFICACION("Pendiente de verificación"),
    APROBADA("Aprobada"),
    RECHAZADA("Rechazada")
}