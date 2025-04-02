package edu.ucne.doers.data.repository

import edu.ucne.doers.data.local.dao.TareaDao
import edu.ucne.doers.data.local.entity.TareaEntity
import edu.ucne.doers.data.local.model.CondicionTarea
import edu.ucne.doers.data.remote.RemoteDataSource
import edu.ucne.doers.data.remote.Resource
import edu.ucne.doers.data.remote.dto.TareaDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TareaRepository @Inject constructor(
    private val tareaDao: TareaDao,
    private val remote: RemoteDataSource
) {
    suspend fun save(tarea: TareaEntity) = tareaDao.save(tarea)

    suspend fun find(id: Int) = tareaDao.find(id)

    fun getAll(): Flow<List<TareaEntity>> = tareaDao.getAll()

    suspend fun delete(tarea: TareaEntity) = tareaDao.delete(tarea)

    fun getActiveTasks(): Flow<List<TareaEntity>> =
        tareaDao.getByCondition(CondicionTarea.ACTIVA)

    /*fun save(tarea: TareaEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            tareaDao.save(tarea)
            remote.saveTarea(tarea.toDto())
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Error al guardar tarea: ${e.localizedMessage}"))
        }
    }

    suspend fun find(id: Int): TareaEntity? {
        return try {
            val local = tareaDao.find(id)
            if (local != null) return local

            val remoteTarea = remote.getTarea(id)
            val entity = remoteTarea.toEntity()
            tareaDao.save(entity)
            entity
        } catch (e: Exception) {
            null
        }
    }

    fun getAll(): Flow<Resource<List<TareaEntity>>> = flow {
        emit(Resource.Loading())

        val localData = tareaDao.getAll().firstOrNull()
        emit(Resource.Success(localData ?: emptyList()))

        try {
            val remoteData = remote.getTareas()
            val entities = remoteData.map { it.toEntity() }
            tareaDao.save(entities)

            val updatedLocal = tareaDao.getAll().firstOrNull()
            emit(Resource.Success(updatedLocal ?: emptyList()))
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener tareas: ${e.localizedMessage}", localData))
        }
    }

    suspend fun delete(tarea: TareaEntity): Resource<Unit> {
        return try {
            tareaDao.delete(tarea)
            remote.deleteTarea(tarea.tareaId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Error al eliminar tarea: ${e.localizedMessage}")
        }
    }

    fun getActiveTasks(): Flow<Resource<List<TareaEntity>>> = flow {
        emit(Resource.Loading())
        val local = tareaDao.getByCondition(CondicionTarea.ACTIVA).firstOrNull()
        emit(Resource.Success(local ?: emptyList()))

        try {
            val remoteData = remote.getTareas()
            val entities = remoteData.map { it.toEntity() }
            tareaDao.save(entities)
            val updated = tareaDao.getByCondition(CondicionTarea.ACTIVA).firstOrNull()
            emit(Resource.Success(updated ?: emptyList()))
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener tareas activas: ${e.localizedMessage}", local))
        }
    }

     */
}

fun TareaEntity.toDto() = TareaDto(
    tareaId = tareaId,
    padreId = padreId,
    descripcion = descripcion,
    imagenURL = imagenURL,
    puntos = puntos,
    estado = estado,
    periodicidad = periodicidad,
    condicion = condicion
)

fun TareaDto.toEntity() = TareaEntity(
    tareaId = tareaId,
    padreId = padreId,
    descripcion = descripcion,
    imagenURL = imagenURL,
    puntos = puntos,
    estado = estado,
    periodicidad = periodicidad,
    condicion = condicion
)