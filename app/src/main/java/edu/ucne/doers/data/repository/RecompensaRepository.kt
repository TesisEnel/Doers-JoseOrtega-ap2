package edu.ucne.doers.data.repository

import edu.ucne.doers.data.local.dao.RecompensaDao
import edu.ucne.doers.data.local.entity.RecompensaEntity
import edu.ucne.doers.data.remote.RemoteDataSource
import edu.ucne.doers.data.remote.Resource
import edu.ucne.doers.data.remote.dto.RecompensaDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RecompensaRepository @Inject constructor(
    private val recompensaDao: RecompensaDao,
    private val remote: RemoteDataSource
) {

    suspend fun save(recompensa: RecompensaEntity) = recompensaDao.save(recompensa)

    suspend fun find(id: Int) = recompensaDao.find(id)

    fun getAll(): Flow<List<RecompensaEntity>> = recompensaDao.getAll()

    fun getRecompensasByPadreId(padreId: String): Flow<List<RecompensaEntity>> = recompensaDao.getRecompensasByPadreId(padreId)

    suspend fun delete(recompensa: RecompensaEntity) = recompensaDao.delete(recompensa)

    /*fun save(recompensa: RecompensaEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            recompensaDao.save(recompensa)
            val dto = recompensa.toDto()
            remote.saveRecompensa(dto)

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Error al guardar recompensa: ${e.localizedMessage}"))
        }
    }

    fun getAll(): Flow<Resource<List<RecompensaEntity>>> = flow {
        emit(Resource.Loading())
        val localData = recompensaDao.getAll().firstOrNull()
        emit(Resource.Success(localData ?: emptyList()))

        try {
            val remoteList = remote.getRecompensas()
            val entities = remoteList.map { it.toEntity() }
            recompensaDao.save(entities)
            val updatedLocal = recompensaDao.getAll().firstOrNull()
            emit(Resource.Success(updatedLocal ?: emptyList()))
        } catch (e: Exception) {
            emit(Resource.Error("Error al cargar recompensas: ${e.localizedMessage}", localData))
        }
    }

    fun getRecompensasByPadreId(padreId: String): Flow<Resource<List<RecompensaEntity>>> = flow {
        emit(Resource.Loading())
        val local = recompensaDao.getRecompensasByPadreId(padreId).firstOrNull()
        emit(Resource.Success(local ?: emptyList()))
        try {
            val remoteList = remote.getRecompensas().filter { it.padreId == padreId }
            val entities = remoteList.map { it.toEntity() }
            recompensaDao.save(entities)
            val updated = recompensaDao.getRecompensasByPadreId(padreId).firstOrNull()
            emit(Resource.Success(updated ?: emptyList()))
        } catch (e: Exception) {
            emit(Resource.Error("No se pudo actualizar recompensas: ${e.localizedMessage}", local))
        }
    }

    suspend fun find(id: Int): RecompensaEntity? {
        return try {
            val local = recompensaDao.find(id)
            if (local != null) return local

            val remote = remote.getRecompensa(id)
            val entity = remote.toEntity()
            recompensaDao.save(entity)
            entity
        } catch (e: Exception) {
            null
        }
    }

    suspend fun delete(recompensa: RecompensaEntity): Resource<Unit> {
        return try {
            recompensaDao.delete(recompensa)
            remote.deleteRecompensa(recompensa.recompensaId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Error al eliminar recompensa: ${e.localizedMessage}")
        }
    }

     */
}

fun RecompensaEntity.toDto() = RecompensaDto(
    recompensaId = recompensaId,
    padreId = padreId,
    descripcion = descripcion,
    imagenURL = imagenURL,
    puntosNecesarios = puntosNecesarios,
    estado = estado
)

