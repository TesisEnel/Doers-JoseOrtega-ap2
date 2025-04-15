package edu.ucne.doers.data.repository

import edu.ucne.doers.data.local.dao.HijoDao
import edu.ucne.doers.data.local.dao.PadreDao
import edu.ucne.doers.data.local.entity.HijoEntity
import edu.ucne.doers.data.remote.RemoteDataSource
import edu.ucne.doers.data.remote.Resource
import edu.ucne.doers.data.remote.dto.HijoDto
import edu.ucne.doers.data.remote.dto.PadreDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HijoRepository @Inject constructor(
    private val hijoDao: HijoDao,
    private val padreDao: PadreDao,
    private val remote: RemoteDataSource
) {

    fun save(hijo: HijoEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        try {
            val existing = hijoDao.findByNombreAndPadreId(hijo.nombre, hijo.padreId)

            if (existing != null) {
                val actualizado = existing.copy(
                    saldoActual = hijo.saldoActual,
                    balance = hijo.balance,
                    fotoPerfil = hijo.fotoPerfil
                )
                hijoDao.save(actualizado)
                remote.updateHijo(actualizado.hijoId, actualizado.toDto())
                emit(Resource.Success(Unit))
                return@flow
            }

            hijoDao.save(hijo)
            remote.saveHijo(hijo.toDto())

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Error al guardar hijo: ${e.localizedMessage}"))
        }
    }

    suspend fun find(id: Int): HijoEntity? {
        return try {
            val local = hijoDao.find(id)
            if (local != null) return local

            val remoteItem = remote.getHijo(id)
            val entity = remoteItem.toEntity()
            hijoDao.save(entity)
            entity
        } catch (e: Exception) {
            null
        }
    }

    fun getAll(): Flow<Resource<List<HijoEntity>>> = flow {
        emit(Resource.Loading())

        val local = hijoDao.getAll().firstOrNull()
        emit(Resource.Success(local ?: emptyList()))

        try {
            val remoteData = remote.getHijos()
            val entities = remoteData.map { it.toEntity() }
            hijoDao.save(entities)

            val updated = hijoDao.getAll().firstOrNull()
            emit(Resource.Success(updated ?: emptyList()))
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener hijos: ${e.localizedMessage}", local))
        }
    }

    suspend fun delete(hijo: HijoEntity): Resource<Unit> {
        return try {
            hijoDao.delete(hijo)
            remote.deleteHijo(hijo.hijoId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Error al eliminar hijo: ${e.localizedMessage}")
        }
    }

    suspend fun saveLocal(hijo: HijoEntity) {
        hijoDao.save(hijo)
    }

    suspend fun loginHijo(nombre: String, codigoSala: String): Resource<HijoEntity> {
        return try {
            val padreDto = remote.getPadreByCodigoSala(codigoSala)
            val padre = padreDto.toEntity()

            val padreLocal = padreDao.find(padre.padreId)
            if (padreLocal == null) {
                padreDao.save(padre)
            }

            val existingHijo = hijoDao.findByNombreAndPadreId(nombre, padre.padreId)
            if (existingHijo != null) {
                return Resource.Success(existingHijo)
            }

            val hijo = HijoEntity(
                padreId = padre.padreId,
                nombre = nombre,
                fotoPerfil = "personaje_1"
            )
            val hijoDto = remote.saveHijo(hijo.toDto())
            val hijoConFoto = if (hijoDto.fotoPerfil.isNullOrBlank()) {
                hijoDto.copy(fotoPerfil = "personaje_1").toEntity()
            } else {
                hijoDto.toEntity()
            }

            hijoDao.save(hijoConFoto)

            Resource.Success(hijoConFoto)

        } catch (e: Exception) {
            Resource.Error("El código de sala no existe o no hay conexión: ${e.localizedMessage}")
        }
    }

    suspend fun getPadreByCodigoSala(codigoSala: String): PadreDto {
        return remote.getPadreByCodigoSala(codigoSala)
    }
}

fun HijoEntity.toDto() = HijoDto(
    hijoId = hijoId,
    padreId = padreId,
    nombre = nombre,
    saldoActual = saldoActual,
    balance = balance,
    fotoPerfil = fotoPerfil
)

fun HijoDto.toEntity() = HijoEntity(
    hijoId = hijoId,
    padreId = padreId,
    nombre = nombre,
    saldoActual = saldoActual,
    balance = balance,
    fotoPerfil = fotoPerfil
)