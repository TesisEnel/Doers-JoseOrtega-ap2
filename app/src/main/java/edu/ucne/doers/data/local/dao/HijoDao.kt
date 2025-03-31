package edu.ucne.doers.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import edu.ucne.doers.data.local.entity.HijoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HijoDao {
    @Upsert
    suspend fun save(hijoEntity: HijoEntity)

    @Upsert()
    suspend fun save(hijoEntity: List<HijoEntity>)

    @Query(
        """
            SELECT *
            FROM "Hijos"
            WHERE hijoId=:id
            LIMIT 1
        """
    )
    suspend fun find(id: Int): HijoEntity?

    @Delete
    suspend fun delete(hijoEntity: HijoEntity)

    @Query("SELECT * FROM Hijos WHERE nombre = :nombre AND padreId = :padreId LIMIT 1")
    suspend fun findByNombreAndPadreId(nombre: String, padreId: String): HijoEntity?

    @Query("SELECT * FROM Hijos")
    fun getAll(): Flow<List<HijoEntity>>

    @Update
    suspend fun update(hijoEntity: HijoEntity)
}