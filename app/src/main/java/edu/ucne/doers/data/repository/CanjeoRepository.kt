package edu.ucne.doers.data.repository

import edu.ucne.doers.data.local.dao.CanjeoDao
import edu.ucne.doers.data.local.entity.CanjeoEntity
import edu.ucne.doers.data.local.model.EstadoCanjeo
import edu.ucne.doers.data.remote.RemoteDataSource
import edu.ucne.doers.data.remote.Resource
import edu.ucne.doers.data.remote.dto.CanjeoDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CanjeoRepository @Inject constructor(
    private val canjeoDao: CanjeoDao,
    private val remote: RemoteDataSource
) {
    fun save(canjeo: CanjeoEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        try {
            val canjeoEntityConId = if (canjeo.canjeoId > 0) {
                val updated = remote.updateCanjeo(canjeo.canjeoId, canjeo.toDto())
                canjeo
            } else {
                val saved = remote.saveCanjeo(canjeo.toDto())
                val entity = saved.toEntity()
                entity
            }
            canjeoDao.save(canjeoEntityConId)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Error al guardar canjeo: ${e.localizedMessage}"))
        }
    }

    suspend fun find(id: Int): CanjeoEntity? {
        return try {
            val local = canjeoDao.find(id)
            if (local != null) return local

            val remote = remote.getCanjeo(id)
            val entity = remote.toEntity()
            canjeoDao.save(entity)
            entity
        } catch (e: Exception) {
            null
        }
    }

    suspend fun delete(canjeo: CanjeoEntity) {
        try {
            canjeoDao.delete(canjeo)
            remote.deleteCanjeo(canjeo.canjeoId)
        } catch (e: Exception) {
            println("Error al eliminar en el API: ${e.message}")
        }
    }

    suspend fun countPendingRewards(recompensaId: Int, hijoId: Int, estado: EstadoCanjeo): Int {
        return canjeoDao.countPendingRewards(recompensaId, hijoId, estado)
    }

    fun getAll(): Flow<Resource<List<CanjeoEntity>>> = flow {
        emit(Resource.Loading())

        val local = canjeoDao.getAll().firstOrNull()
        emit(Resource.Success(local ?: emptyList()))

        try {
            val remoteData = remote.getCanjeos()
            val entities = remoteData.map { it.toEntity() }
            canjeoDao.save(entities)

            val updated = canjeoDao.getAll().firstOrNull()
            emit(Resource.Success(updated ?: emptyList()))
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener canjeos: ${e.localizedMessage}", local))
        }
    }
}

fun CanjeoDto.toEntity() = CanjeoEntity(
    canjeoId = this.canjeoId,
    recompensaId = this.recompensaId,
    hijoId = this.hijoId,
    estado = this.estado,
    fecha = this.fecha
)

fun CanjeoEntity.toDto(): CanjeoDto {
    return CanjeoDto(
        canjeoId = this.canjeoId,
        recompensaId = this.recompensaId,
        hijoId = this.hijoId,
        estado = this.estado,
        fecha = this.fecha
    )
}