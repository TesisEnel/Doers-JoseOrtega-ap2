package edu.ucne.doers.data.repository

import android.annotation.SuppressLint
import android.util.Log
import edu.ucne.doers.data.local.dao.CanjeoDao
import edu.ucne.doers.data.local.entity.CanjeoEntity
import edu.ucne.doers.data.remote.RemoteDataSource
import edu.ucne.doers.data.remote.Resource
import edu.ucne.doers.data.remote.dto.CanjeoDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

class CanjeoRepository @Inject constructor(
    private val canjeoDao: CanjeoDao,
    private val remoteDataSource: RemoteDataSource
) {
    fun save(canjeo: CanjeoEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        Log.d("CanjeoRepository", "Guardando localmente: $canjeo")

        try {
            canjeoDao.save(canjeo)
            Log.d("CanjeoRepository", "Guardado local exitoso. Enviando al servidor: ${canjeo.toDto()}")

            remoteDataSource.saveCanjeo(canjeo.toDto())
            Log.d("CanjeoRepository", "Guardado remoto exitoso")
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            Log.e("CanjeoRepository", "Error al guardar: ${e.message}", e)
            emit(Resource.Error("Error al guardar canjeo: ${e.localizedMessage}"))
        }
    }

    suspend fun find(id: Int): CanjeoEntity? {
        return try {
            val local = canjeoDao.find(id)
            if (local != null) return local

            val remote = remoteDataSource.getCanjeo(id)
            val entity = remote.toEntity()
            canjeoDao.save(entity)
            entity
        } catch (e: Exception) {
            null
        }
    }

    fun getAll(): Flow<Resource<List<CanjeoEntity>>> = flow {
        emit(Resource.Loading())
        val localData = canjeoDao.getAll().firstOrNull()
        emit(Resource.Success(localData ?: emptyList()))

        try {
            val remoteData = remoteDataSource.getCanjeos()
            val entities = remoteData.map { it.toEntity() }
            canjeoDao.save(entities)
            val updatedLocal = canjeoDao.getAll().firstOrNull()
            emit(Resource.Success(updatedLocal ?: emptyList()))
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener canjeos: ${e.localizedMessage}", localData))
        }
    }

    suspend fun delete(canjeo: CanjeoEntity) {
        try {
            canjeoDao.delete(canjeo)
            remoteDataSource.deleteCanjeo(canjeo.canjeoId)
        } catch (e: Exception) {
            println("Error al eliminar en el API: ${e.message}")
        }
    }

}

fun CanjeoDto.toEntity() = CanjeoEntity(
    canjeoId = this.canjeoId,
    hijoId = this.hijoId,
    recompensaId = this.recompensaId,
    fecha = Date(),
    estado = this.estado
)

@SuppressLint("SimpleDateFormat")
fun CanjeoEntity.toDto(): CanjeoDto {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    return CanjeoDto(
        canjeoId = this.canjeoId,
        hijoId = this.hijoId,
        recompensaId = this.recompensaId,
        fecha = dateFormat.format(fecha),
        estado = this.estado
    )
}