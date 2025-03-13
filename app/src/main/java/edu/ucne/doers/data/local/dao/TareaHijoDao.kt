package edu.ucne.doers.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.doers.data.local.entity.TareaHijoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaHijoDao {
    @Upsert
    suspend fun save(tareaHijoEntity: TareaHijoEntity)

    @Upsert()
    suspend fun save(tareaHijoEntity: List<TareaHijoEntity>)

    @Query(
        """
            SELECT *
            FROM "TareasHijos"
            WHERE tareaHijoID=:id
            LIMIT 1
        """
    )
    suspend fun find(id: Int): TareaHijoEntity?

    @Delete
    suspend fun delete(tareaHijoEntity: TareaHijoEntity)

    @Query("SELECT * FROM TareasHijos")
    fun getAll(): Flow<List<TareaHijoEntity>>
}