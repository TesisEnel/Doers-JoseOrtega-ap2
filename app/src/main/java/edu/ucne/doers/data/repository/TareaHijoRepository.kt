package edu.ucne.doers.data.repository

import edu.ucne.doers.data.local.dao.TareaHijoDao
import edu.ucne.doers.data.local.entity.TareaHijo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TareaHijoRepository @Inject constructor(
    private val tareaHijoDao: TareaHijoDao
) {
    suspend fun save(tareaHijo: TareaHijo) = tareaHijoDao.save(tareaHijo)

    suspend fun find(id: Int) = tareaHijoDao.find(id)

    fun getAll(): Flow<List<TareaHijo>> = tareaHijoDao.getAll()

    suspend fun delete(tareaHijo: TareaHijo) = tareaHijoDao.delete(tareaHijo)
}