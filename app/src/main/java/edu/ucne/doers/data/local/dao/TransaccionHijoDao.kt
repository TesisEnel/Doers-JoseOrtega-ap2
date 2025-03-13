package edu.ucne.doers.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.doers.data.local.entity.TransaccionHijo
import kotlinx.coroutines.flow.Flow

@Dao
interface TransaccionHijoDao {
    @Upsert
    suspend fun save(transaccionHijo: TransaccionHijo)

    @Upsert()
    suspend fun save(transaccionHijo: List<TransaccionHijo>)

    @Query(
        """
            SELECT *
            FROM "TransaccionesHijo"
            WHERE transaccionID=:id
            LIMIT 1
        """
    )
    suspend fun find(id: Int): TransaccionHijo?

    @Delete
    suspend fun delete(transaccionHijo: TransaccionHijo)

    @Query("SELECT * FROM transaccioneshijo")
    fun getAll(): Flow<List<TransaccionHijo>>
}