package edu.ucne.doers.di

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import edu.ucne.doers.data.local.model.EstadoTarea
import edu.ucne.doers.data.local.model.EstadoTareaHijo
import edu.ucne.doers.data.local.model.TipoTransaccion
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Adapter {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

    // Date conversion methods
    @ToJson
    fun toJson(value: Date?): String? {
        return value?.let { dateFormat.format(it) }
    }

    @FromJson
    fun fromJson(value: String?): Date? {
        return value?.let { dateFormat.parse(it) }
    }
    @ToJson
    fun fromEstadoTareaToJson(value: EstadoTarea): String = value.name

    @FromJson
    fun fromJsonToEstadoTarea(value: String): EstadoTarea = EstadoTarea.valueOf(value)

    @ToJson
    fun fromEstadoTareaHijoToJson(value: EstadoTareaHijo): String = value.name

    @FromJson
    fun fromJsonToEstadoTareaHijo(value: String): EstadoTareaHijo = EstadoTareaHijo.valueOf(value)

    @ToJson
    fun fromTipoTransaccionToJson(value: TipoTransaccion): String = value.name

    @FromJson
    fun fromJsonToTipoTransaccion(value: String): TipoTransaccion = TipoTransaccion.valueOf(value)
}