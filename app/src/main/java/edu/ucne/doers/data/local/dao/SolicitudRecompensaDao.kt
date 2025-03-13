package edu.ucne.doers.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.doers.data.local.entity.SolicitudRecompensa
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudRecompensaDao {
    @Upsert
    suspend fun save(solicitudRecompensa: SolicitudRecompensa)

    @Upsert()
    suspend fun save(solicitudRecompensa: List<SolicitudRecompensa>)

    @Query(
        """
            SELECT *
            FROM "SolicitudesRecompensas"
            WHERE solicitudId=:id
            LIMIT 1
        """
    )
    suspend fun find(id: Int): SolicitudRecompensa?

    @Delete
    suspend fun delete(solicitudRecompensa: SolicitudRecompensa)

    @Query("SELECT * FROM SolicitudesRecompensas")
    fun getAll(): Flow<List<SolicitudRecompensa>>
}