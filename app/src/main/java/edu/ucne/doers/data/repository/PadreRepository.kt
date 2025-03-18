package edu.ucne.doers.data.repository

import android.util.Log
import edu.ucne.doers.data.local.dao.PadreDao
import edu.ucne.doers.data.local.entity.PadreEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class PadreRepository @Inject constructor(
    private val padreDao: PadreDao,
    private val authRepository: AuthRepository
) {
    suspend fun save(padre: PadreEntity) = padreDao.save(padre)

    suspend fun find(id: String) = padreDao.find(id)

    fun getAll(): Flow<List<PadreEntity>> = padreDao.getAll()

    suspend fun delete(padre: PadreEntity) = padreDao.delete(padre)

    suspend fun findEmail(email: String?): PadreEntity? { // Acepta String? y maneja null
        return if (email != null) {
            padreDao.findEmail(email)
        } else {
            null
        }
    }
    suspend fun getCurrentUser(): PadreEntity? {
        val userData = authRepository.getUser()
        val userId = userData?.userId
        return if (userId != null) {
            val padre = padreDao.find(userId)
            if (padre == null) {
                Log.w("PadreRepository", "No se encontró PadreEntity para userId: $userId")
                null
            } else {
                Log.d("PadreRepository", "PadreEntity encontrado: $padre")
                padre
            }
        } else {
            Log.e("PadreRepository", "No se pudo obtener el userId del usuario autenticado")
            null
        }
    }
}