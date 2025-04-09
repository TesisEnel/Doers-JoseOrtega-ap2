package edu.ucne.doers.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import edu.ucne.doers.data.local.model.EstadoTareaHijo
import java.util.Date

@Entity(
    tableName = "Canjeos",
    foreignKeys = [
        ForeignKey(entity = HijoEntity::class, parentColumns = ["hijoId"], childColumns = ["hijoId"], onDelete = ForeignKey.NO_ACTION),
        ForeignKey(entity = RecompensaEntity::class, parentColumns = ["recompensaId"], childColumns = ["recompensaId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["hijoId", "recompensaId"])]
)
data class CanjeoEntity(
    @PrimaryKey(autoGenerate = true) val canjeoId: Int = 0,
    val hijoId: Int,
    val recompensaId: Int,
    val fecha: Date,
    val estado: EstadoTareaHijo = EstadoTareaHijo.PENDIENTE_VERIFICACION
)