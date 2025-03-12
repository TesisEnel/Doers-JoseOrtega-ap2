package edu.ucne.doers.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.doers.data.local.entity.TareaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {
    @Upsert
    suspend fun save(tareaEntity: TareaEntity)

    @Upsert()
    suspend fun save(tareaEntity: List<TareaEntity>)

    @Query(
        """
            SELECT *
            FROM "Tareas"
            WHERE tareaID=:id
            LIMIT 1
        """
    )
    suspend fun find(id: Int): TareaEntity?

    @Delete
    suspend fun delete(tareaEntity: TareaEntity)

    @Query("SELECT * FROM Tareas")
    fun getAll(): Flow<List<TareaEntity>>
}