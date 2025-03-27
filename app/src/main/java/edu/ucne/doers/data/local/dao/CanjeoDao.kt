package edu.ucne.doers.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.doers.data.local.entity.CanjeoEntity
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
}