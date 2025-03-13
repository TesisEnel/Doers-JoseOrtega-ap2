package edu.ucne.doers.data.repository

import edu.ucne.doers.data.local.dao.CanjeoDao
import edu.ucne.doers.data.local.entity.CanjeoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CanjeoRepository @Inject constructor(
    private val canjeoDao: CanjeoDao
) {
    suspend fun save(canjeo: CanjeoEntity) = canjeoDao.save(canjeo)

    suspend fun find(id: Int) = canjeoDao.find(id)

    fun getAll(): Flow<List<CanjeoEntity>> = canjeoDao.getAll()

    suspend fun delete(canjeo: CanjeoEntity) = canjeoDao.delete(canjeo)
}