package edu.ucne.doers.data.repository

import edu.ucne.doers.data.local.dao.HijoDao
import edu.ucne.doers.data.local.dao.PadreDao
import edu.ucne.doers.data.local.dao.TareaHijoDao
import edu.ucne.doers.data.local.entity.HijoEntity
import edu.ucne.doers.data.local.entity.TareaHijo
import edu.ucne.doers.data.remote.RemoteDataSource
import edu.ucne.doers.data.remote.Resource
import edu.ucne.doers.data.remote.dto.HijoDto
import edu.ucne.doers.presentation.extension.collectFirstOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HijoRepository @Inject constructor(
    private val hijoDao: HijoDao,
    private val tareaHijoDao: TareaHijoDao,
    private val padreDao: PadreDao,
    private val padreRepository: PadreRepository,
    private val remote: RemoteDataSource
) {

    suspend fun save(hijo: HijoEntity) = hijoDao.save(hijo)

    suspend fun find(id: Int) = hijoDao.find(id)

    fun getAll(): Flow<List<HijoEntity>> = hijoDao.getAll()

    suspend fun delete(hijo: HijoEntity) = hijoDao.delete(hijo)

    suspend fun findByNombreAndPadreId(nombre: String, padreId: String): HijoEntity? {
        return hijoDao.findByNombreAndPadreId(nombre, padreId)
    }

    suspend fun getPadreIdByCodigoSala(codigoSala: String): String? {
        val padre = padreRepository.getAll().collectFirstOrNull()?.find { it.codigoSala == codigoSala }
        return padre?.padreId
    }

    suspend fun loginHijo(nombre: String, codigoSala: String): Resource<Boolean> {
        val padre = padreDao.findByCodigoSala(codigoSala)
        return if (padre != null) {
            val hijo = HijoEntity(padreId = padre.padreId, nombre = nombre)
            hijoDao.save(hijo)
            Resource.Success(true)
        } else {
            Resource.Error("El código de sala no existe")
        }
    }

    /*fun save(hijo: HijoEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
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

    suspend fun findByNombreAndPadreId(nombre: String, padreId: String): HijoEntity? {
        return hijoDao.findByNombreAndPadreId(nombre, padreId)
    }

    suspend fun getPadreIdByCodigoSala(codigoSala: String): String? {
        val local = padreDao.findByCodigoSala(codigoSala)
        if (local != null) return local.padreId

        return try {
            val padreDto = remote.getPadreByCodigoSala(codigoSala)
            val padre = padreDto.toEntity()
            padreDao.save(padre)
            padre.padreId
        } catch (e: Exception) {
            null
        }
    }

    suspend fun insertTareaHijo(tareaHijo: TareaHijo): Resource<Unit> {
        return try {
            tareaHijoDao.save(tareaHijo)
            remote.saveTareaHijo(tareaHijo.toDto())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Error al insertar tarea del hijo: ${e.localizedMessage}")
        }
    }

    fun getTareasHijo(hijoId: Int): Flow<List<TareaHijo>> = tareaHijoDao.getByHijoId(hijoId)

    suspend fun loginHijo(nombre: String, codigoSala: String): Resource<Boolean> {
        return try {
            val padreDto = remote.getPadreByCodigoSala(codigoSala)
            val padre = padreDto.toEntity()

            padreDao.save(padre)

            val hijo = HijoEntity(padreId = padre.padreId, nombre = nombre)
            hijoDao.save(hijo)

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error("El código de sala no existe o no hay conexión: ${e.localizedMessage}")
        }
    }

     */
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