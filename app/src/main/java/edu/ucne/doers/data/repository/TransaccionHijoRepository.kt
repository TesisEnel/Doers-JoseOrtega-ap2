package edu.ucne.doers.data.repository

import edu.ucne.doers.data.local.dao.TransaccionHijoDao
import edu.ucne.doers.data.local.entity.TransaccionHijo
import edu.ucne.doers.data.remote.RemoteDataSource
import edu.ucne.doers.data.remote.Resource
import edu.ucne.doers.data.remote.dto.TransaccionHijoDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TransaccionHijoRepository @Inject constructor(
    private val transaccionHijoDao: TransaccionHijoDao,
    private val remote: RemoteDataSource
) {
    suspend fun save(transaccionHijo: TransaccionHijo) = transaccionHijoDao.save(transaccionHijo)

    suspend fun find(id: Int) = transaccionHijoDao.find(id)

    fun getAll(): Flow<List<TransaccionHijo>> = transaccionHijoDao.getAll()

    suspend fun delete(transaccionHijo: TransaccionHijo) = transaccionHijoDao.delete(transaccionHijo)


    /*fun save(transaccionHijo: TransaccionHijo): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        try {
            transaccionHijoDao.save(transaccionHijo)
            remote.saveTransaccionHijo(transaccionHijo.toDto())
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Error al guardar transacción: ${e.localizedMessage}"))
        }
    }

    suspend fun find(id: Int): TransaccionHijo? {
        return try {
            val local = transaccionHijoDao.find(id)
            if (local != null) return local

            val remoteItem = remote.getTransaccionHijo(id)
            val entity = remoteItem.toEntity()
            transaccionHijoDao.save(entity)
            entity
        } catch (e: Exception) {
            null
        }
    }

    fun getAll(): Flow<Resource<List<TransaccionHijo>>> = flow {
        emit(Resource.Loading())

        val local = transaccionHijoDao.getAll().firstOrNull()
        emit(Resource.Success(local ?: emptyList()))

        try {
            val remoteData = remote.getTransaccionesHijo()
            val entities = remoteData.map { it.toEntity() }
            transaccionHijoDao.save(entities)

            val updated = transaccionHijoDao.getAll().firstOrNull()
            emit(Resource.Success(updated ?: emptyList()))
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener transacciones: ${e.localizedMessage}", local))
        }
    }

    suspend fun delete(transaccionHijo: TransaccionHijo): Resource<Unit> {
        return try {
            transaccionHijoDao.delete(transaccionHijo)
            remote.deleteTransaccionHijo(transaccionHijo.transaccionID)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Error al eliminar transacción: ${e.localizedMessage}")
        }
    }

     */
}

fun TransaccionHijo.toDto() = TransaccionHijoDto(
    transaccionID = transaccionID,
    hijoId = hijoId,
    tipo = tipo,
    monto = monto,
    descripcion = descripcion,
    fecha = fecha
)

fun TransaccionHijoDto.toEntity() = TransaccionHijo(
    transaccionID = transaccionID,
    hijoId = hijoId,
    tipo = tipo,
    monto = monto,
    descripcion = descripcion,
    fecha = fecha
)
