package edu.ucne.doers.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import edu.ucne.doers.data.local.model.EstadoTarea

@Entity(
    tableName = "Tareas",
    foreignKeys = [ForeignKey(
        entity = PadreEntity::class,
        parentColumns = ["padreId"],
        childColumns = ["padreId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["padreId"])]
)
data class TareaEntity(
    @PrimaryKey(autoGenerate = true) val tareaId: Int = 0,
    val padreId: Int,
    val descripcion: String,
    val imagenURL: String?,
    val puntos: Int,
    val estado: EstadoTarea = EstadoTarea.PENDIENTE
)