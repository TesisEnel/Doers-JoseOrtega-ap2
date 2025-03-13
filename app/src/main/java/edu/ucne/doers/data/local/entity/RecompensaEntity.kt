package edu.ucne.doers.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import edu.ucne.doers.data.local.model.EstadoRecompensa

@Entity(
    tableName = "Recompensas",
    foreignKeys = [ForeignKey(
        entity = PadreEntity::class,
        parentColumns = ["padreId"],
        childColumns = ["padreId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["padreId"])]
)
data class RecompensaEntity(
    @PrimaryKey(autoGenerate = true) val recompensaId: Int = 0,
    val padreId: String,
    val descripcion: String,
    val imagenURL: String?,
    val puntosNecesarios: Int,
    val estado: EstadoRecompensa = EstadoRecompensa.DISPONIBLE
)