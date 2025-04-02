package edu.ucne.doers.data.repository

import android.util.Log
import edu.ucne.doers.data.local.dao.CanjeoDao
import edu.ucne.doers.data.local.entity.CanjeoEntity
import edu.ucne.doers.data.remote.RemoteDataSource
import edu.ucne.doers.data.remote.Resource
import edu.ucne.doers.data.remote.dto.CanjeoDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CanjeoRepository @Inject constructor(
    private val canjeoDao: CanjeoDao,
    private val remoteDataSource: RemoteDataSource
) {
    fun save(canjeo: CanjeoEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        try {
            canjeoDao.save(canjeo)
            remoteDataSource.saveCanjeo(canjeo.toDto())
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Error al guardar canjeo: ${e.localizedMessage}"))
        }
    }

    suspend fun find(id: Int): CanjeoEntity? {
        return try {
            val local = canjeoDao.find(id)
            if (local != null) return local

            val remote = remoteDataSource.getCanjeo(id)
            val entity = remote.toEntity()
            canjeoDao.save(entity)
            entity
        } catch (e: Exception) {
            null
        }
    }

    fun getAll(): Flow<Resource<List<CanjeoEntity>>> = flow {
        emit(Resource.Loading())

        // Emitir primero los datos locales (para mostrar algo rápido)
        val localData = canjeoDao.getAll().firstOrNull()
        emit(Resource.Success(localData ?: emptyList()))

        try {
            // Consultar la API aunque haya datos locales
            val remoteData = remoteDataSource.getCanjeos()
            val entities = remoteData.map { it.toEntity() }

            // Actualizar Room con los datos más recientes
            canjeoDao.save(entities)

            // Emitir nuevamente los datos actualizados desde Room
            val updatedLocal = canjeoDao.getAll().firstOrNull()
            emit(Resource.Success(updatedLocal ?: emptyList()))
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener canjeos: ${e.localizedMessage}", localData))
        }
    }

    suspend fun delete(canjeo: CanjeoEntity) {
        try {
            // Eliminar primero en local
            canjeoDao.delete(canjeo)

            // Luego intentar eliminar en el API
            remoteDataSource.deleteCanjeo(canjeo.canjeoId)
        } catch (e: Exception) {
            // Puedes guardar un "log de eliminación pendiente" para sincronizar más tarde
            println("Error al eliminar en el API: ${e.message}")
        }
    }
}

fun CanjeoDto.toEntity() = CanjeoEntity(
    canjeoId = this.canjeoId,
    hijoId = this.hijoId,
    recompensaId = this.recompensaId,
    fecha = this.fecha
)

fun CanjeoEntity.toDto() = CanjeoDto(
    canjeoId = this.canjeoId,
    hijoId = this.hijoId,
    recompensaId = this.recompensaId,
    fecha = this.fecha
)