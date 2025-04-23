package edu.ucne.doers.data.repository

import android.util.Log
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
    fun save(recompensa: RecompensaEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        try {
            val recompensaEntityConId = if (recompensa.recompensaId > 0) {
                remote.updateRecompensa(recompensa.recompensaId, recompensa.toDto())
                recompensa
            } else {
                val savedDto = remote.saveRecompensa(recompensa.toDto())
                val entity = savedDto.toEntity()
                if (entity.recompensaId <= 0) {
                    throw IllegalStateException("El servidor no asignó un ID válido a la recompensa")
                }
                entity
            }

            try {
                recompensaDao.save(recompensaEntityConId)
                Log.d(
                    "RecompensaRepository",
                    "Recompensa guardada localmente: $recompensaEntityConId"
                )
            } catch (e: Exception) {
                Log.e("RecompensaRepository", "Error al guardar en Room: ${e.localizedMessage}")
                emit(Resource.Error("Error al guardar localmente: ${e.localizedMessage}"))
                return@flow
            }

            emit(Resource.Success(Unit))

        } catch (e: Exception) {
            Log.e("RecompensaRepository", "Error al guardar en API: ${e.localizedMessage}")
            emit(Resource.Error("Error al guardar en el servidor: ${e.localizedMessage}"))
        }
    }

    fun getAll(padreId: String): Flow<Resource<List<RecompensaEntity>>> = flow {
        try {
            emit(Resource.Loading())

            val remoteList = remote.getRecompensas()

            val filteredRemote = remoteList.filter { it.padreId == padreId }

            val entities = filteredRemote.map { it.toEntity() }
            recompensaDao.save(entities)

            val updatedLocal = recompensaDao.getByPadreId(padreId).firstOrNull()
            emit(Resource.Success(updatedLocal ?: emptyList()))
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener recompensas: ${e.localizedMessage}"))
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

