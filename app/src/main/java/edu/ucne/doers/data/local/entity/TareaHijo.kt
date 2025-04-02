package edu.ucne.doers.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import edu.ucne.doers.data.local.model.EstadoTareaHijo
import java.util.Date

@Entity(
    tableName = "TareasHijos",
    foreignKeys = [
        ForeignKey(
            entity = TareaEntity::class,
            parentColumns = ["tareaId"],
            childColumns = ["tareaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = HijoEntity::class,
            parentColumns = ["hijoId"],
            childColumns = ["hijoId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["tareaId", "hijoId"])]
)
data class TareaHijo(
    @PrimaryKey(autoGenerate = true) val tareaHijoId: Int = 0,
    val tareaId: Int,
    val hijoId: Int? = null,
    val estado: EstadoTareaHijo = EstadoTareaHijo.PENDIENTE_VERIFICACION,
    val fechaVerificacion: Date? = null
)