package edu.ucne.doers.data.repository

import edu.ucne.doers.data.local.dao.SolicitudRecompensaDao
import edu.ucne.doers.data.local.entity.SolicitudRecompensa
import edu.ucne.doers.data.remote.RemoteDataSource
import edu.ucne.doers.data.remote.Resource
import edu.ucne.doers.data.remote.dto.SolicitudRecompensaDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SolicitudRecompensaRepository @Inject constructor(
    private val solicitudRecomensaDao: SolicitudRecompensaDao,
    private val remote: RemoteDataSource
) {

    suspend fun save(solicitudRecompensa: SolicitudRecompensa) = solicitudRecomensaDao.save(solicitudRecompensa)

    suspend fun find(id: Int) = solicitudRecomensaDao.find(id)

    fun getAll(): Flow<List<SolicitudRecompensa>> = solicitudRecomensaDao.getAll()

    suspend fun delete(solicitudRecompensa: SolicitudRecompensa) = solicitudRecomensaDao.delete(solicitudRecompensa)

    /*fun save(solicitudRecompensa: SolicitudRecompensa): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            solicitudRecomensaDao.save(solicitudRecompensa)
            remote.saveSolicitudRecompensa(solicitudRecompensa.toDto())
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Error al guardar solicitud: ${e.localizedMessage}"))
        }
    }

    fun getAll(): Flow<Resource<List<SolicitudRecompensa>>> = flow {
        emit(Resource.Loading())

        val local = solicitudRecomensaDao.getAll().firstOrNull()
        emit(Resource.Success(local ?: emptyList()))

        try {
            val remoteData = remote.getSolicitudesRecompensas()
            val entities = remoteData.map { it.toEntity() }
            solicitudRecomensaDao.save(entities)

            val updated = solicitudRecomensaDao.getAll().firstOrNull()
            emit(Resource.Success(updated ?: emptyList()))
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener solicitudes: ${e.localizedMessage}", local))
        }
    }

    suspend fun find(id: Int): SolicitudRecompensa? {
        return try {
            val local = solicitudRecomensaDao.find(id)
            if (local != null) return local

            val remoteItem = remote.getSolicitudRecompensa(id)
            val entity = remoteItem.toEntity()
            solicitudRecomensaDao.save(entity)
            entity
        } catch (e: Exception) {
            null
        }
    }

    suspend fun delete(solicitudRecompensa: SolicitudRecompensa): Resource<Unit> {
        return try {
            solicitudRecomensaDao.delete(solicitudRecompensa)
            remote.deleteSolicitudRecompensa(solicitudRecompensa.solicitudId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Error al eliminar solicitud: ${e.localizedMessage}")
        }
    }

     */
}

fun SolicitudRecompensa.toDto() = SolicitudRecompensaDto(
    solicitudId = solicitudId,
    recompensaId = recompensaId,
    hijoId = hijoId,
    fecha = fecha,
    estado = estado
)

fun SolicitudRecompensaDto.toEntity() = SolicitudRecompensa(
    solicitudId = solicitudId,
    recompensaId = recompensaId,
    hijoId = hijoId,
    fecha = fecha,
    estado = estado
)
