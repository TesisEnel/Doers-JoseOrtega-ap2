package edu.ucne.doers.data.remote

import edu.ucne.doers.data.remote.dto.CanjeoDto
import edu.ucne.doers.data.remote.dto.HijoDto
import edu.ucne.doers.data.remote.dto.PadreDto
import edu.ucne.doers.data.remote.dto.RecompensaDto
import edu.ucne.doers.data.remote.dto.TareaDto
import edu.ucne.doers.data.remote.dto.TareaHijoDto
import edu.ucne.doers.data.remote.dto.TransaccionHijoDto
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val doersApi: DoersApi
) {
    suspend fun getCanjeos() = doersApi.getCanjeos()
    suspend fun getCanjeo(id: Int) = doersApi.getCanjeo(id)
    suspend fun saveCanjeo(canjeoDto: CanjeoDto) = doersApi.saveCanjeo(canjeoDto)
    suspend fun updateCanjeo(id: Int, canjeoDto: CanjeoDto) = doersApi.updateCanjeo(id, canjeoDto)
    suspend fun deleteCanjeo(id: Int) = doersApi.deleteCanjeo(id)

    suspend fun getHijos() = doersApi.getHijos()
    suspend fun getHijo(id: Int) = doersApi.getHijo(id)
    suspend fun saveHijo(hijoDto: HijoDto) = doersApi.saveHijo(hijoDto)
    suspend fun updateHijo(id: Int, hijoDto: HijoDto) = doersApi.updateHijo(id, hijoDto)
    suspend fun deleteHijo(id: Int) = doersApi.deleteHijo(id)

    suspend fun getPadres() = doersApi.getPadres()
    suspend fun getPadre(id: String) = doersApi.getPadre(id)
    suspend fun savePadre(padreDto: PadreDto) {
        try {
            val existing = doersApi.getPadre(padreDto.padreId)
            doersApi.updatePadre(padreDto.padreId, padreDto)
        } catch (e: Exception) {
            doersApi.savePadre(padreDto)
        }
    }
    suspend fun updatePadre(id: String, padreDto: PadreDto) = doersApi.updatePadre(id, padreDto)
    suspend fun deletePadre(id: String) = doersApi.deletePadre(id)
    suspend fun getPadreByCodigoSala(codigoSala: String): PadreDto = doersApi.getPadreByCodigoSala(codigoSala)

    suspend fun getRecompensas() = doersApi.getRecompensas()
    suspend fun getRecompensa(id: Int) = doersApi.getRecompensa(id)
    suspend fun saveRecompensa(recompensaDto: RecompensaDto) = doersApi.saveRecompensa(recompensaDto)
    suspend fun updateRecompensa(id: Int, recompensaDto: RecompensaDto) = doersApi.updateRecompensa(id, recompensaDto)
    suspend fun deleteRecompensa(id: Int) = doersApi.deleteRecompensa(id)
    suspend fun getRecompensasActivas() = doersApi.getRecompensasActivas()

    suspend fun getTareas() = doersApi.getTareas()
    suspend fun getTarea(id: Int) = doersApi.getTarea(id)
    suspend fun saveTarea(tareaDto: TareaDto) = doersApi.saveTarea(tareaDto)
    suspend fun updateTarea(id: Int, tareaDto: TareaDto) = doersApi.updateTarea(id, tareaDto)
    suspend fun deleteTarea(id: Int) = doersApi.deleteTarea(id)
    suspend fun getTareasActivas() = doersApi.getTareasActivas()

    suspend fun getTareasHijos() = doersApi.getTareasHijos()
    suspend fun getTareaHijo(id: Int) = doersApi.getTareaHijo(id)
    suspend fun saveTareaHijo(tareaHijoDto: TareaHijoDto) = doersApi.saveTareaHijo(tareaHijoDto)
    suspend fun updateTareaHijo(id: Int, tareaHijoDto: TareaHijoDto) = doersApi.updateTareaHijo(id, tareaHijoDto)
    suspend fun deleteTareaHijo(id: Int) = doersApi.deleteTareaHijo(id)

    suspend fun getTransaccionesHijo() = doersApi.getTransaccionesHijo()
    suspend fun getTransaccionHijo(id: Int) = doersApi.getTransaccionHijo(id)
    suspend fun saveTransaccionHijo(transaccionHijoDto: TransaccionHijoDto) = doersApi.saveTransaccionHijo(transaccionHijoDto)
    suspend fun updateTransaccionHijo(id: Int, transaccionHijoDto: TransaccionHijoDto) = doersApi.updateTransaccionHijo(id, transaccionHijoDto)
    suspend fun deleteTransaccionHijo(id: Int) = doersApi.deleteTransaccionHijo(id)
}