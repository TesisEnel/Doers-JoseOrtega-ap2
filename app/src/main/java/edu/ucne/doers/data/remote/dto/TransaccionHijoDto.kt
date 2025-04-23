package edu.ucne.doers.data.remote.dto

import edu.ucne.doers.data.local.model.TipoTransaccion
import java.util.Date

data class TransaccionHijoDto(
    val transaccionId: Int,
    val hijoId: Int,
    val tipo: TipoTransaccion,
    val monto: Int,
    val descripcion: String?,
    val fecha: Date
)