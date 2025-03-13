package edu.ucne.doers.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Padres")
data class PadreEntity(
    @PrimaryKey val padreId: String,
    val nombre: String,
    val profilePictureUrl: String?,
    val email: String,
    val codigoSala: String,
)