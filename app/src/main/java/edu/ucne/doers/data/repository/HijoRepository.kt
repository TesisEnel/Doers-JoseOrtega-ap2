package edu.ucne.doers.data.repository

import edu.ucne.doers.data.local.dao.HijoDao
import edu.ucne.doers.data.local.dao.PadreDao
import edu.ucne.doers.data.local.dao.TareaHijoDao
import edu.ucne.doers.data.local.entity.HijoEntity
import edu.ucne.doers.data.local.entity.TareaHijo
import edu.ucne.doers.data.remote.Resource
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class HijoRepository @Inject constructor(
    private val hijoDao: HijoDao,
    private val tareaHijoDao: TareaHijoDao,
    private val padreDao: PadreDao
) {
    suspend fun save(hijo: HijoEntity) = hijoDao.save(hijo)

    suspend fun find(id: Int) = hijoDao.find(id)

    fun getAll(): Flow<List<HijoEntity>> = hijoDao.getAll()

    suspend fun delete(hijo: HijoEntity) = hijoDao.delete(hijo)

    suspend fun insertTareaHijo(tareaHijo: TareaHijo) = tareaHijoDao.save(tareaHijo)

    fun getTareasHijo(hijoId: Int): Flow<List<TareaHijo>> = tareaHijoDao.getByHijoId(hijoId)
    
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