package edu.ucne.doers.data.repository

import edu.ucne.doers.data.local.dao.SolicitudRecompensaDao
import edu.ucne.doers.data.local.entity.SolicitudRecompensa
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SolicitudRecompensaRepository @Inject constructor(
    private val solicitudRecomensaDao: SolicitudRecompensaDao
) {
    suspend fun save(solicitudRecompensa: SolicitudRecompensa) = solicitudRecomensaDao.save(solicitudRecompensa)

    suspend fun find(id: Int) = solicitudRecomensaDao.find(id)

    fun getAll(): Flow<List<SolicitudRecompensa>> = solicitudRecomensaDao.getAll()

    suspend fun delete(solicitudRecompensa: SolicitudRecompensa) = solicitudRecomensaDao.delete(solicitudRecompensa)
}