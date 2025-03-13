package edu.ucne.doers.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.doers.data.local.entity.PadreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PadreDao {
    @Upsert
    suspend fun save(padreEntity: PadreEntity)

    @Upsert()
    suspend fun save(padreEntity: List<PadreEntity>)

    @Query(
        """
            SELECT *
            FROM "Padres"
            WHERE padreId=:id
            LIMIT 1
        """
    )
    suspend fun find(id: Int): PadreEntity?

    @Delete
    suspend fun delete(padreEntity: PadreEntity)

    @Query("SELECT * FROM Padres")
    fun getAll(): Flow<List<PadreEntity>>
}