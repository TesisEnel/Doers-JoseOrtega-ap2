package edu.ucne.doers.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Hijos",
    foreignKeys = [ForeignKey(
        entity = SalaEntity::class,
        parentColumns = ["salaID"],
        childColumns = ["salaID"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["salaId"])]
)
data class HijoEntity(
    @PrimaryKey(autoGenerate = true) val hijoID: Int = 0,
    val salaID: Int,
    val nombre: String,
    val saldoActual: Int = 0
)