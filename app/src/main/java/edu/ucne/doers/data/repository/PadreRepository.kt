package edu.ucne.doers.data.repository

import edu.ucne.doers.data.local.dao.PadreDao
import edu.ucne.doers.data.local.entity.PadreEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class PadreRepository @Inject constructor(
    private val padreDao: PadreDao
) {
    suspend fun save(padre: PadreEntity) = padreDao.save(padre)

    suspend fun find(id: String) = padreDao.find(id)

    fun getAll(): Flow<List<PadreEntity>> = padreDao.getAll()

    suspend fun delete(padre: PadreEntity) = padreDao.delete(padre)
}