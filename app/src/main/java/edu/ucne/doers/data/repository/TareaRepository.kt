package edu.ucne.doers.data.repository

import edu.ucne.doers.data.local.dao.TareaDao
import edu.ucne.doers.data.local.entity.TareaEntity
import edu.ucne.doers.data.local.model.CondicionTarea
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TareaRepository @Inject constructor(
    private val tareaDao: TareaDao
) {
    suspend fun save(tarea: TareaEntity) = tareaDao.save(tarea)

    suspend fun find(id: Int) = tareaDao.find(id)

    fun getAll(): Flow<List<TareaEntity>> = tareaDao.getAll()

    suspend fun delete(tarea: TareaEntity) = tareaDao.delete(tarea)

    fun getActiveTasks(): Flow<List<TareaEntity>> =
        tareaDao.getByCondition(CondicionTarea.ACTIVA)
}