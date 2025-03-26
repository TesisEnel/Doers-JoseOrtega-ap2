package edu.ucne.doers.data.repository

import edu.ucne.doers.data.local.dao.HijoDao
import edu.ucne.doers.data.local.dao.TareaHijoDao
import edu.ucne.doers.data.local.entity.HijoEntity
import edu.ucne.doers.data.local.entity.TareaHijo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HijoRepository @Inject constructor(
    private val hijoDao: HijoDao,
    private val tareaHijoDao: TareaHijoDao
) {
    suspend fun save(hijo: HijoEntity) = hijoDao.save(hijo)

    suspend fun find(id: Int) = hijoDao.find(id)

    fun getAll(): Flow<List<HijoEntity>> = hijoDao.getAll()

    suspend fun delete(hijo: HijoEntity) = hijoDao.delete(hijo)

    suspend fun insertTareaHijo(tareaHijo: TareaHijo) = tareaHijoDao.save(tareaHijo)

    fun getTareasHijo(hijoId: Int): Flow<List<TareaHijo>> = tareaHijoDao.getByHijoId(hijoId)
}