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
    fun save(recompensa: RecompensaEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            // Guardar local
            recompensaDao.save(recompensa)

            // Enviar al API
            val dto = recompensa.toDto()
            remote.saveRecompensa(dto)

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Error al guardar recompensa: ${e.localizedMessage}"))
        }
    }

    fun getAll(): Flow<Resource<List<RecompensaEntity>>> = flow {
        emit(Resource.Loading())

        // Emitimos primero lo local para mostrar algo al instante
        val localData = recompensaDao.getAll().firstOrNull()
        emit(Resource.Success(localData ?: emptyList()))

        try {
            // Siempre intentar actualizar desde el API
            val remoteList = remote.getRecompensas()
            val entities = remoteList.map { it.toEntity() }

            // Guardamos lo más reciente en Room
            recompensaDao.save(entities)

            // Volvemos a emitir lo más actualizado
            val updatedLocal = recompensaDao.getAll().firstOrNull()
            emit(Resource.Success(updatedLocal ?: emptyList()))
        } catch (e: Exception) {
            // Si falla la conexión, se mantiene lo local emitido antes
            emit(Resource.Error("Error al cargar recompensas: ${e.localizedMessage}", localData))
        }
    }

    fun getRecompensasByPadreId(padreId: String): Flow<Resource<List<RecompensaEntity>>> = flow {
        emit(Resource.Loading())

        // Emitimos primero lo que haya en local (aunque esté desactualizado)
        val local = recompensaDao.getRecompensasByPadreId(padreId).firstOrNull()
        emit(Resource.Success(local ?: emptyList()))
        try {
            // Siempre consulta al API si hay conexión
            val remoteList = remote.getRecompensas().filter { it.padreId == padreId }
            val entities = remoteList.map { it.toEntity() }

            // Actualizamos Room con los datos del API
            recompensaDao.save(entities)

            // Emitimos lo más reciente desde Room
            val updated = recompensaDao.getRecompensasByPadreId(padreId).firstOrNull()
            emit(Resource.Success(updated ?: emptyList()))
        } catch (e: Exception) {
            // Solo mostramos error si falló el fetch remoto
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
}

fun RecompensaEntity.toDto() = RecompensaDto(
    recompensaId = recompensaId,
    padreId = padreId,
    descripcion = descripcion,
    imagenURL = imagenURL,
    puntosNecesarios = puntosNecesarios,
    estado = estado
)

fun RecompensaDto.toEntity() = RecompensaEntity(
    recompensaId = recompensaId,
    padreId = padreId,
    descripcion = descripcion,
    imagenURL = imagenURL,
    puntosNecesarios = puntosNecesarios,
    estado = estado
)