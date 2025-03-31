package edu.ucne.doers.data.repository

import edu.ucne.doers.data.local.dao.HijoDao
import edu.ucne.doers.data.local.dao.PadreDao
import edu.ucne.doers.data.local.dao.TareaHijoDao
import edu.ucne.doers.data.local.entity.HijoEntity
import edu.ucne.doers.data.local.entity.TareaHijo
import edu.ucne.doers.data.remote.Resource
import edu.ucne.doers.presentation.extension.collectFirstOrNull
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HijoRepository @Inject constructor(
    private val hijoDao: HijoDao,
    private val tareaHijoDao: TareaHijoDao,
    private val padreDao: PadreDao,
    private val padreRepository: PadreRepository
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

    suspend fun updateHijo(hijo: HijoEntity) = hijoDao.update(hijo)
}