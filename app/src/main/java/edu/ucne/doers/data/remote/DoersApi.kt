package edu.ucne.doers.data.remote

import edu.ucne.doers.data.remote.dto.CanjeoDto
import edu.ucne.doers.data.remote.dto.HijoDto
import edu.ucne.doers.data.remote.dto.PadreDto
import edu.ucne.doers.data.remote.dto.RecompensaDto
import edu.ucne.doers.data.remote.dto.TareaDto
import edu.ucne.doers.data.remote.dto.TareaHijoDto
import edu.ucne.doers.data.remote.dto.TransaccionHijoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DoersApi {
    @GET("api/Canjeos")
    suspend fun getCanjeos(): List<CanjeoDto>

    @GET("api/Canjeos/{id}")
    suspend fun getCanjeo(@Path("id") id: Int): CanjeoDto

    @POST("api/Canjeos")
    suspend fun saveCanjeo(@Body canjeoDto: CanjeoDto): CanjeoDto

    @PUT("api/Canjeos/{id}")
    suspend fun updateCanjeo(
        @Path("id") canjeoId: Int,
        @Body canjeoDto: CanjeoDto
    ): Response<CanjeoDto>

    @DELETE("api/Canjeos/{id}")
    suspend fun deleteCanjeo(@Path("id") id: Int): Response<Unit>

    @GET("api/Hijos")
    suspend fun getHijos(): List<HijoDto>

    @GET("api/Hijos/{id}")
    suspend fun getHijo(@Path("id") id: Int): HijoDto

    @POST("api/Hijos")
    suspend fun saveHijo(@Body hijoDto: HijoDto): HijoDto

    @PUT("api/Hijos/{id}")
    suspend fun updateHijo(@Path("id") hijoId: Int, @Body hijoDto: HijoDto): Response<HijoDto>

    @DELETE("api/Hijos/{id}")
    suspend fun deleteHijo(@Path("id") id: Int): Response<Unit>

    @GET("api/Padres")
    suspend fun getPadres(): List<PadreDto>

    @GET("api/Padres/{id}")
    suspend fun getPadre(@Path("id") id: String): PadreDto

    @POST("api/Padres")
    suspend fun savePadre(@Body padreDto: PadreDto): PadreDto

    @PUT("api/Padres/{id}")
    suspend fun updatePadre(@Path("id") padreId: String, @Body padreDto: PadreDto): Response<PadreDto>

    @DELETE("api/Padres/{id}")
    suspend fun deletePadre(@Path("id") id: String): Response<Unit>

    @GET("api/Padres/by-codigo-sala/{codigoSala}")
    suspend fun getPadreByCodigoSala(@Path("codigoSala") codigoSala: String): PadreDto

    @GET("api/Recompensas")
    suspend fun getRecompensas(): List<RecompensaDto>

    @GET("api/Recompensas/{id}")
    suspend fun getRecompensa(@Path("id") id: Int): RecompensaDto

    @POST("api/Recompensas")
    suspend fun saveRecompensa(@Body recompensaDto: RecompensaDto): RecompensaDto

    @PUT("api/Recompensas/{id}")
    suspend fun updateRecompensa(@Path("id") recompensaId: Int, @Body recompensaDto: RecompensaDto): Response<RecompensaDto>

    @DELETE("api/Recompensas/{id}")
    suspend fun deleteRecompensa(@Path("id") id: Int): Response<Unit>

    @GET("api/Tareas")
    suspend fun getTareas(): List<TareaDto>

    @GET("api/Tareas/{id}")
    suspend fun getTarea(@Path("id") id: Int): TareaDto

    @POST("api/Tareas")
    suspend fun saveTarea(@Body tareaDto: TareaDto): TareaDto

    @PUT("api/Tareas/{id}")
    suspend fun updateTarea(@Path("id") tareaId: Int, @Body tareaDto: TareaDto): Response<TareaDto>

    @DELETE("api/Tareas/{id}")
    suspend fun deleteTarea(@Path("id") id: Int): Response<Unit>

    @GET("api/Tareas/activas")
    suspend fun getTareasActivas(): List<TareaDto>

    @GET("api/TareasHijos")
    suspend fun getTareasHijos(): List<TareaHijoDto>

    @GET("api/TareasHijos/{id}")
    suspend fun getTareaHijo(@Path("id") id: Int): TareaHijoDto

    @POST("api/TareasHijos")
    suspend fun saveTareaHijo(@Body tareaHijoDto: TareaHijoDto): TareaHijoDto

    @PUT("api/TareasHijos/{id}")
    suspend fun updateTareaHijo(@Path("id") tareaHijoId: Int, @Body tareaHijoDto: TareaHijoDto): Response<TareaHijoDto>

    @DELETE("api/TareasHijos/{id}")
    suspend fun deleteTareaHijo(@Path("id") id: Int): Response<Unit>

    @GET("api/TransaccionesHijos")
    suspend fun getTransaccionesHijo(): List<TransaccionHijoDto>

    @GET("api/TransaccionesHijos/{id}")
    suspend fun getTransaccionHijo(@Path("id") id: Int): TransaccionHijoDto

    @POST("api/TransaccionesHijos")
    suspend fun saveTransaccionHijo(@Body transaccionHijoDto: TransaccionHijoDto): TransaccionHijoDto

    @PUT("api/TransaccionesHijos/{id}")
    suspend fun updateTransaccionHijo(@Path("id") transaccionID: Int, @Body transaccionHijoDto: TransaccionHijoDto): Response<TransaccionHijoDto>

    @DELETE("api/TransaccionesHijos/{id}")
    suspend fun deleteTransaccionHijo(@Path("id") id: Int): Response<Unit>
}