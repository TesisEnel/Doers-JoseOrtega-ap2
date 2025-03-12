package edu.ucne.doers.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import edu.ucne.doers.data.local.model.EstadoRecompensa
import java.util.Date

@Entity(
    tableName = "Recompensas",
    foreignKeys = [ForeignKey(
        entity = Sala::class,
        parentColumns = ["salaID"],
        childColumns = ["salaID"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["salaID"])]
)
data class Recompensa(
    @PrimaryKey(autoGenerate = true) val recompensaID: Int = 0,
    val salaID: Int,
    val nombre: String,
    val imagenURL: String?,
    val puntosNecesarios: Int,
    val tiempoLimite: Date?,
    val estado: EstadoRecompensa = EstadoRecompensa.DISPONIBLE
)
