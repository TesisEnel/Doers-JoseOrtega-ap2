package edu.ucne.doers.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.doers.data.local.entity.TareaHijo
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaHijoDao {
    @Upsert
    suspend fun save(tareaHijo: TareaHijo)

    @Upsert()
    suspend fun save(tareaHijo: List<TareaHijo>)

    @Query(
        """
            SELECT *
            FROM "TareasHijos"
            WHERE tareaHijoId=:id
            LIMIT 1
        """
    )

    suspend fun find(id: Int): TareaHijo?

    @Delete
    suspend fun delete(tareaHijo: TareaHijo)

    @Query
        ("""
        SELECT * FROM TareasHijos 
        WHERE hijoId = :hijoId
    """)
    fun getByHijoId(hijoId: Int): Flow<List<TareaHijo>>

    @Query("SELECT * FROM TareasHijos")
    fun getAll(): Flow<List<TareaHijo>>
}