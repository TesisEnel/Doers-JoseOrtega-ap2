package edu.ucne.doers.data.local.model

enum class EstadoTarea(val nombreMostrable: String) {
    PENDIENTE("Pendiente"),
    COMPLETADA("Completada"),
    CANCELADA("Cancelada")
}

enum class EstadoTareaHijo(val nombreMostrable: String) {
    PENDIENTE_VERIFICACION("Pendiente de verificación"),
    APROBADA("Aprobada"),
    RECHAZADA("Rechazada")
}