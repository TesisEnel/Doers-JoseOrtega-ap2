package edu.ucne.doers.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Salas",
    foreignKeys = [ForeignKey(
        entity = PadreEntity::class,
        parentColumns = ["padreID"],
        childColumns = ["padreID"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["padreId"])]
)
data class SalaEntity(
    @PrimaryKey(autoGenerate = true) val salaID: Int = 0,
    val codigoSala: String,
    val padreID: String
)