package edu.ucne.doers.data.repository

import android.util.Log
import edu.ucne.doers.data.local.dao.TareaHijoDao
import edu.ucne.doers.data.local.entity.TareaHijo
import edu.ucne.doers.data.local.model.EstadoTareaHijo
import edu.ucne.doers.data.remote.RemoteDataSource
import edu.ucne.doers.data.remote.Resource
import edu.ucne.doers.data.remote.dto.TareaHijoDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TareaHijoRepository @Inject constructor(
    private val tareaHijoDao: TareaHijoDao,
    private val remote: RemoteDataSource
) {
    fun save(tareaHijo: TareaHijo): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        try {
            Log.d("TareaHijoRepository", "Iniciando guardado de TareaHijo: $tareaHijo")
            val tareaHijoEntityConId = if (tareaHijo.tareaHijoId > 0) {
                val updated = remote.updateTareaHijo(tareaHijo.tareaHijoId, tareaHijo.toDto())
                Log.d("TareaHijoRepository", "TareaHijo actualizada en API: $updated")
                tareaHijo
            } else {
                val saved = remote.saveTareaHijo(tareaHijo.toDto())
                Log.d("TareaHijoRepository", "TareaHijo guardada en API: $saved")
                val entity = saved.toEntity()
                Log.d("TareaHijoRepository", "Convertido a entidad: $entity")
                entity
            }

            Log.d("TareaHijoRepository", "Guardando en Room: $tareaHijoEntityConId")
            tareaHijoDao.save(tareaHijoEntityConId)
            Log.d("TareaHijoRepository", "Guardado en.Concurrent Room completado")
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            Log.e("TareaHijoRepository", "Error al guardar: ${e.localizedMessage}", e)
            emit(Resource.Error("Error al guardar tarea hijo: ${e.localizedMessage}"))
        }
    }

    fun getAll(): Flow<Resource<List<TareaHijo>>> = flow {
        emit(Resource.Loading())

        val local = tareaHijoDao.getAll().firstOrNull()
        emit(Resource.Success(local ?: emptyList()))

        try {
            val remoteData = remote.getTareasHijos()
            val entities = remoteData.map { it.toEntity() }
            tareaHijoDao.save(entities)

            val updated = tareaHijoDao.getAll().firstOrNull()
            emit(Resource.Success(updated ?: emptyList()))
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener tareas hijos: ${e.localizedMessage}", local))
        }
    }

    suspend fun find(id: Int): TareaHijo? {
        return try {
            val local = tareaHijoDao.find(id)
            if (local != null) return local

            val remoteItem = remote.getTareaHijo(id)
            val entity = remoteItem.toEntity()
            tareaHijoDao.save(entity)
            entity
        } catch (e: Exception) {
            null
        }
    }

    suspend fun delete(tareaHijo: TareaHijo): Resource<Unit> {
        return try {
            tareaHijoDao.delete(tareaHijo)
            remote.deleteTareaHijo(tareaHijo.tareaHijoId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Error al eliminar tarea hijo: ${e.localizedMessage}")
        }
    }

    suspend fun countPendingTasks(tareaId: Int, hijoId: Int, estado: EstadoTareaHijo): Int {
        return tareaHijoDao.countPendingTasks(tareaId, hijoId, estado)
    }
}

fun TareaHijo.toDto() = TareaHijoDto(
    tareaHijoId = tareaHijoId,
    tareaId = tareaId,
    hijoId = hijoId,
    estado = estado,
    fechaVerificacion = fechaVerificacion
)

fun TareaHijoDto.toEntity(): TareaHijo = TareaHijo(
    tareaHijoId = tareaHijoId,
    tareaId = tareaId,
    hijoId = hijoId,
    estado = estado,
    fechaVerificacion = fechaVerificacion
)