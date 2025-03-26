package edu.ucne.doers.data.repository

import edu.ucne.doers.data.local.dao.HijoDao
import edu.ucne.doers.data.local.dao.PadreDao
import edu.ucne.doers.data.local.entity.HijoEntity
import edu.ucne.doers.data.remote.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HijoRepository @Inject constructor(
    private val hijoDao: HijoDao,
    private val padreDao: PadreDao
) {
    suspend fun save(hijo: HijoEntity) = hijoDao.save(hijo)

    suspend fun find(id: Int) = hijoDao.find(id)

    fun getAll(): Flow<List<HijoEntity>> = hijoDao.getAll()

    suspend fun delete(hijo: HijoEntity) = hijoDao.delete(hijo)

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
}