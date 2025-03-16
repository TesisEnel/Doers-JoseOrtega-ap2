package edu.ucne.doers.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import edu.ucne.doers.data.local.model.EstadoTareaHijo
import java.util.Date

@Entity(
    tableName = "SolicitudesRecompensas",
    foreignKeys = [
        ForeignKey(
            entity = RecompensaEntity::class,
            parentColumns = ["recompensaId"],
            childColumns = ["recompensaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = HijoEntity::class,
            parentColumns = ["hijoId"],
            childColumns = ["hijoId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["recompensaId", "hijoId"])]
)
data class SolicitudRecompensa(
    @PrimaryKey(autoGenerate = true) val solicitudId: Int = 0,
    val recompensaId: Int,
    val hijoId: Int,
    val fecha: Date,
    val estado: EstadoTareaHijo = EstadoTareaHijo.PENDIENTE_VERIFICACION
)