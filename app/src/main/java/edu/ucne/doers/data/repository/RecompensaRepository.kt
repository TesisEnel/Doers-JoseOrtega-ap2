package edu.ucne.doers.data.repository

import edu.ucne.doers.data.local.dao.RecompensaDao
import edu.ucne.doers.data.local.entity.RecompensaEntity
import edu.ucne.doers.data.local.entity.TareaEntity
import edu.ucne.doers.data.local.model.CondicionRecompensa
import edu.ucne.doers.data.local.model.CondicionTarea
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
    fun save(recompensa: RecompensaEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        try {
            val recompensaEntityConId = if (recompensa.recompensaId > 0) {
                remote.updateRecompensa(recompensa.recompensaId, recompensa.toDto())
                recompensa
            } else {
                val saved = remote.saveRecompensa(recompensa.toDto())
                saved.toEntity()
            }

            recompensaDao.save(recompensaEntityConId)
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
            emit(Resource.Error("Error al obtener recompensas: ${e.localizedMessage}", localData))
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

    fun getActiveRewards(): Flow<Resource<List<RecompensaEntity>>> = flow {
        emit(Resource.Loading())
        val local = recompensaDao.getByCondition(CondicionRecompensa.ACTIVA).firstOrNull()
        emit(Resource.Success(local ?: emptyList()))

        try {
            val remoteData = remote.getRecompensasActivas()
            val entities = remoteData.map { it.toEntity() }
            recompensaDao.save(entities)
            val updated = recompensaDao.getByCondition(CondicionRecompensa.ACTIVA).firstOrNull()
            emit(Resource.Success(updated ?: emptyList()))
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener recompensas activas: ${e.localizedMessage}", local))
        }
    }
}

fun RecompensaEntity.toDto() = RecompensaDto(
    recompensaId = this.recompensaId,
    padreId = this.padreId,
    descripcion = this.descripcion,
    imagenURL = this.imagenURL,
    puntosNecesarios = this.puntosNecesarios,
    estado = this.estado,
    condicion = this.condicion
)

fun RecompensaDto.toEntity() = RecompensaEntity(
    recompensaId = this.recompensaId,
    padreId = this.padreId,
    descripcion = this.descripcion,
    imagenURL = this.imagenURL,
    puntosNecesarios = this.puntosNecesarios,
    estado = this.estado,
    condicion = this.condicion
)
