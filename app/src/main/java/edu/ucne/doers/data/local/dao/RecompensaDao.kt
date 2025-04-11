package edu.ucne.doers.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.doers.data.local.entity.RecompensaEntity
import edu.ucne.doers.data.local.model.CondicionRecompensa
import edu.ucne.doers.data.local.model.CondicionTarea
import kotlinx.coroutines.flow.Flow

@Dao
interface RecompensaDao {
    @Upsert
    suspend fun save(recompensaEntity: RecompensaEntity)

    @Upsert
    suspend fun save(recompensaEntity: List<RecompensaEntity>)

    @Query(
        """
            SELECT *
            FROM "Recompensas"
            WHERE recompensaId=:id
            LIMIT 1
        """
    )
    suspend fun find(id: Int): RecompensaEntity?

    @Query("SELECT * FROM Recompensas WHERE padreId = :padreId")
    fun getByPadreId(padreId: String): Flow<List<RecompensaEntity>>

    @Delete
    suspend fun delete(recompensaEntity: RecompensaEntity)

    @Query("SELECT * FROM Recompensas")
    fun getAll(): Flow<List<RecompensaEntity>>

    @Query(
        """
            SELECT * FROM Recompensas 
            WHERE condicion = :condicion
        """
    )
    fun getByCondition(condicion: CondicionRecompensa): Flow<List<RecompensaEntity>>
}