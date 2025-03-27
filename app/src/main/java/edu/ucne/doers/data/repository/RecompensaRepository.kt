package edu.ucne.doers.data.repository

import edu.ucne.doers.data.local.dao.RecompensaDao
import edu.ucne.doers.data.local.entity.RecompensaEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecompensaRepository @Inject constructor(
    private val recompensaDao: RecompensaDao
) {
    suspend fun save(recompensa: RecompensaEntity) = recompensaDao.save(recompensa)

    suspend fun find(id: Int) = recompensaDao.find(id)

    fun getAll(): Flow<List<RecompensaEntity>> = recompensaDao.getAll()

    fun getRecompensasByPadreId(padreId: String): Flow<List<RecompensaEntity>> = recompensaDao.getRecompensasByPadreId(padreId)

    suspend fun delete(recompensa: RecompensaEntity) = recompensaDao.delete(recompensa)
}