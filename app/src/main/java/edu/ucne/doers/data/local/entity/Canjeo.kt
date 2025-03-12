package edu.ucne.doers.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "Canjeos",
    foreignKeys = [
        ForeignKey(entity = Hijo::class, parentColumns = ["hijoID"], childColumns = ["hijoID"], onDelete = ForeignKey.NO_ACTION),
        ForeignKey(entity = Recompensa::class, parentColumns = ["recompensaID"], childColumns = ["recompensaID"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["hijoID", "recompensaID"])]
)
data class Canjeo(
    @PrimaryKey(autoGenerate = true) val canjeoID: Int = 0,
    val hijoID: Int,
    val recompensaID: Int,
    val fechaCanjeo: Date
)
