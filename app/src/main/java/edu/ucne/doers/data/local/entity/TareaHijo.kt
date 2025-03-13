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
            parentColumns = ["tareaID"],
            childColumns = ["tareaID"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = HijoEntity::class,
            parentColumns = ["hijoID"],
            childColumns = ["hijoID"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["tareaID", "hijoID"])]
)
data class TareaHijo(
    @PrimaryKey(autoGenerate = true) val tareaHijoID: Int = 0,
    val tareaID: Int,
    val hijoID: Int,
    val estado: EstadoTareaHijo = EstadoTareaHijo.PENDIENTE_VERIFICACION,
    val fechaVerificacion: Date? = null,
    val saldoActual: Int = 0,
    val balance: Int = 0
)