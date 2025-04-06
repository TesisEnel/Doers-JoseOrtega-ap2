package edu.ucne.doers.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import edu.ucne.doers.data.local.model.EstadoRecompensa
import edu.ucne.doers.presentation.recompensa.RecompensaUiState

@Entity(
    tableName = "Recompensas",
    foreignKeys = [
        ForeignKey(
            entity = PadreEntity::class,
            parentColumns = ["padreId"],
            childColumns = ["padreId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = HijoEntity::class,
            parentColumns = ["hijoId"],
            childColumns = ["hijoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["padreId"]),
        Index(value = ["hijoId"])
    ]
)
data class RecompensaEntity(
    @PrimaryKey(autoGenerate = true) val recompensaId: Int = 0,
    val padreId: String,
    val hijoId: Int,
    val descripcion: String,
    val imagenURL: String,
    val puntosNecesarios: Int,
    val estado: String
) {
    fun toUiState() = RecompensaUiState(
        recompensaId = recompensaId,
        padreId = padreId,
        imagenURL = imagenURL,
        descripcion = descripcion,
        puntosNecesarios = puntosNecesarios,
        estado = EstadoRecompensa.valueOf(estado),
        errorMessage = null,
        recompensas = emptyList(),
        isLoading = false
    )
}