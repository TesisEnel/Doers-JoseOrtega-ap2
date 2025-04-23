package edu.ucne.doers.data.repository

import edu.ucne.doers.data.local.dao.PadreDao
import edu.ucne.doers.data.local.entity.PadreEntity
import edu.ucne.doers.data.remote.RemoteDataSource
import edu.ucne.doers.data.remote.Resource
import edu.ucne.doers.data.remote.dto.PadreDto
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class PadreRepository @Inject constructor(
    private val padreDao: PadreDao,
    private val remote: RemoteDataSource,
    private val authRepository: AuthRepository
) {

    fun save(padre: PadreEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        try {
            val existing = padreDao.find(padre.padreId)

            if (existing != null) {
                val actualizado = existing.copy(
                    nombre = padre.nombre,
                    email = padre.email,
                    profilePictureUrl = padre.profilePictureUrl,
                    codigoSala = padre.codigoSala
                )
                padreDao.save(actualizado)
                remote.updatePadre(actualizado.padreId, actualizado.toDto())
                emit(Resource.Success(Unit))
                return@flow
            }

            padreDao.save(padre)
            remote.savePadre(padre.toDto())

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Error al guardar padre: ${e.localizedMessage}"))
        }
    }

    suspend fun find(id: String): PadreEntity? {
        return try {
            val local = padreDao.find(id)
            if (local != null) return local

            val remotePadre = remote.getPadre(id)
            val entity = remotePadre.toEntity()
            padreDao.save(entity)
            entity
        } catch (e: Exception) {
            null
        }
    }

    fun getAll(): Flow<Resource<List<PadreEntity>>> = flow {
        emit(Resource.Loading())
        val localData = padreDao.getAll().firstOrNull()
        emit(Resource.Success(localData ?: emptyList()))

        try {
            val remoteData = remote.getPadres()
            val localEntities = remoteData.map { it.toEntity() }
            padreDao.save(localEntities)
            val updatedLocal = padreDao.getAll().firstOrNull()
            emit(Resource.Success(updatedLocal ?: emptyList()))
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener padres: ${e.localizedMessage}", localData))
        }
    }

    suspend fun delete(padre: PadreEntity): Resource<Unit> {
        return try {
            padreDao.delete(padre)
            remote.deletePadre(padre.padreId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Error al eliminar padre: ${e.localizedMessage}")
        }
    }

    suspend fun getCurrentUser(): PadreEntity? {
        return try {
            val userData = authRepository.getUser()
            val userId = userData?.userId ?: return null

            val localPadre = padreDao.find(userId)
            if (localPadre != null) return localPadre

            val remotePadre = remote.getPadre(userId)
            val entity = remotePadre.toEntity()
            padreDao.save(entity)
            entity
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getByCodigoSala(codigoSala: String): PadreEntity? {
        return try {
            remote.getPadreByCodigoSala(codigoSala).toEntity()
        } catch (e: Exception) {
            null
        }
    }
}

fun PadreDto.toEntity() = PadreEntity(
    padreId = this.padreId,
    nombre = this.nombre,
    profilePictureUrl = this.profilePictureUrl,
    email = this.email,
    codigoSala = this.codigoSala
)

fun PadreEntity.toDto() = PadreDto(
    padreId = this.padreId,
    nombre = this.nombre,
    profilePictureUrl = this.profilePictureUrl,
    email = this.email,
    codigoSala = this.codigoSala
)