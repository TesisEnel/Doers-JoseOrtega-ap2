package edu.ucne.doers.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Padres")
data class Padre(
    @PrimaryKey val padreID: String, // UUID en String
    val nombre: String,
    val telefono: String?,
    val email: String
)
