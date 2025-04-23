package edu.ucne.doers.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.doers.data.local.entity.CanjeoEntity
import edu.ucne.doers.data.local.model.EstadoCanjeo
import edu.ucne.doers.data.local.model.EstadoTareaHijo
import kotlinx.coroutines.flow.Flow

@Dao
interface CanjeoDao {
    @Upsert
    suspend fun save(canjeoEntity: CanjeoEntity)

    @Upsert()
    suspend fun save(canjeoEntity: List<CanjeoEntity>)

    @Query(
        """
            SELECT *
            FROM "Canjeos"
            WHERE canjeoId=:id
            LIMIT 1
        """
    )
    suspend fun find(id: Int): CanjeoEntity?

    @Delete
    suspend fun delete(canjeoEntity: CanjeoEntity)

    @Query("SELECT * FROM Canjeos")
    fun getAll(): Flow<List<CanjeoEntity>>

    @Query
        ("""
        SELECT * FROM Canjeos 
        WHERE hijoId = :hijoId
    """)
    fun getByHijoId(hijoId: Int): Flow<List<CanjeoEntity>>

    @Query("SELECT * FROM Canjeos WHERE hijoId = :hijoId AND estado = :estado")
    fun getByHijoIdAndEstado(hijoId: Int, estado: EstadoCanjeo): Flow<List<CanjeoEntity>>

    @Query("""
        SELECT COUNT(*) FROM Canjeos 
        WHERE recompensaId = :tareaId AND hijoId = :hijoId AND estado = :estado
    """)
    suspend fun countPendingRewards(tareaId: Int, hijoId: Int, estado: EstadoCanjeo): Int
}