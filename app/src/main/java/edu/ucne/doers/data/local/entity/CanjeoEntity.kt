package edu.ucne.doers.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "Canjeos",
    foreignKeys = [
        ForeignKey(entity = HijoEntity::class, parentColumns = ["hijoID"], childColumns = ["hijoID"], onDelete = ForeignKey.NO_ACTION),
        ForeignKey(entity = RecompensaEntity::class, parentColumns = ["recompensaID"], childColumns = ["recompensaID"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["hijoID", "recompensaID"])]
)
data class CanjeoEntity(
    @PrimaryKey(autoGenerate = true) val canjeoId: Int = 0,
    val hijoId: Int,
    val recompensaId: Int,
    val fecha: Date
)