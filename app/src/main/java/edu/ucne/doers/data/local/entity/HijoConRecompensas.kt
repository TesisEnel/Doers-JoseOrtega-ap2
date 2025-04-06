package edu.ucne.doers.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class HijoConRecompensas(
    @Embedded val hijo: HijoEntity,
    @Relation(
        parentColumn = "hijoId",
        entityColumn = "hijoId"
    )
    val recompensas: List<RecompensaEntity>
)