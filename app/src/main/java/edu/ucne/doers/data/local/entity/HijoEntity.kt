package edu.ucne.doers.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Hijos",
    foreignKeys = [ForeignKey(
        entity = PadreEntity::class,
        parentColumns = ["padreId"],
        childColumns = ["padreId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["padreId"])]
)
data class HijoEntity(
    @PrimaryKey(autoGenerate = true) val hijoId: Int = 0,
    val padreId: String,
    val nombre: String,
    val saldoActual: Int = 0,
    val balance: Int = 0
)