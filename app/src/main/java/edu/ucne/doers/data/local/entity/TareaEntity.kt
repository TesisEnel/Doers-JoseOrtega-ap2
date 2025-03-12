package edu.ucne.doers.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import edu.ucne.doers.data.local.model.EstadoTarea
import java.util.Date

@Entity(
    tableName = "Tareas",
    foreignKeys = [ForeignKey(
        entity = SalaEntity::class,
        parentColumns = ["salaID"],
        childColumns = ["salaID"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["salaID"])]
)
data class TareaEntity(
    @PrimaryKey(autoGenerate = true) val tareaID: Int = 0,
    val salaID: Int,
    val nombre: String,
    val descripcion: String,
    val imagenURL: String?,
    val puntos: Int,
    val fechaLimite: Date,
    val estado: EstadoTarea = EstadoTarea.PENDIENTE
)