package edu.ucne.doers.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import edu.ucne.doers.data.local.model.TipoTransaccion
import java.util.Date

@Entity(
    tableName = "TransaccionesHijo",
    foreignKeys = [ForeignKey(
        entity = HijoEntity::class,
        parentColumns = ["hijoId"],
        childColumns = ["hijoId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["hijoId"])]
)
data class TransaccionHijo(
    @PrimaryKey(autoGenerate = true) val transaccionID: Int = 0,
    val hijoId: Int,
    val tipo: TipoTransaccion,
    val monto: Int,
    val descripcion: String?,
    val fecha: Date
)