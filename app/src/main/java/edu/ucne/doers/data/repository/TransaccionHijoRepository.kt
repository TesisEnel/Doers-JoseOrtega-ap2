package edu.ucne.doers.data.repository

import edu.ucne.doers.data.local.dao.TransaccionHijoDao
import edu.ucne.doers.data.local.entity.TransaccionHijo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransaccionHijoRepository @Inject constructor(
    private val transaccionHijoDao: TransaccionHijoDao
) {
    suspend fun save(transaccionHijo: TransaccionHijo) = transaccionHijoDao.save(transaccionHijo)

    suspend fun find(id: Int) = transaccionHijoDao.find(id)

    fun getAll(): Flow<List<TransaccionHijo>> = transaccionHijoDao.getAll()

    suspend fun delete(transaccionHijo: TransaccionHijo) = transaccionHijoDao.delete(transaccionHijo)
}