package edu.ucne.doers.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.doers.data.local.entity.TransaccionHijoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransaccionHijoDao {
    @Upsert
    suspend fun save(transaccionHijoEntity: TransaccionHijoEntity)

    @Upsert()
    suspend fun save(transaccionHijoEntity: List<TransaccionHijoEntity>)

    @Query(
        """
            SELECT *
            FROM "TransaccionesHijo"
            WHERE transaccionID=:id
            LIMIT 1
        """
    )
    suspend fun find(id: Int): TransaccionHijoEntity?

    @Delete
    suspend fun delete(transaccionHijoEntity: TransaccionHijoEntity)

    @Query("SELECT * FROM transaccioneshijo")
    fun getAll(): Flow<List<TransaccionHijoEntity>>
}