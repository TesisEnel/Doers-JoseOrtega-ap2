package edu.ucne.doers.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import edu.ucne.doers.data.local.model.CondicionTarea
import edu.ucne.doers.data.local.model.EstadoTarea
import edu.ucne.doers.data.local.model.PeriodicidadTarea

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
    val padreId: String,
    val descripcion: String,
    val puntos: Int,
    val estado: EstadoTarea = EstadoTarea.PENDIENTE,
    val periodicidad: PeriodicidadTarea? = null,
    val condicion: CondicionTarea = CondicionTarea.INACTIVA
)