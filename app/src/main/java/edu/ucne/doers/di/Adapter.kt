package edu.ucne.doers.di

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import edu.ucne.doers.data.local.model.EstadoRecompensa
import edu.ucne.doers.data.local.model.EstadoTarea
import edu.ucne.doers.data.local.model.EstadoTareaHijo
import edu.ucne.doers.data.local.model.TipoTransaccion
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Adapter {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

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

    @ToJson
    fun fromEstadoRecompensaToJson(value: EstadoRecompensa): String = value.name

    @FromJson
    fun fromJsonToEstadoRecompensa(value: String): EstadoRecompensa = EstadoRecompensa.valueOf(value)

    @ToJson
    fun fromCondicionTareaToJson(value: edu.ucne.doers.data.local.model.CondicionTarea): String = value.name

    @FromJson
    fun fromJsonToCondicionTarea(value: String): edu.ucne.doers.data.local.model.CondicionTarea =
        edu.ucne.doers.data.local.model.CondicionTarea.valueOf(value.uppercase())

    @ToJson
    fun fromPeriodicidadTareaToJson(value: edu.ucne.doers.data.local.model.PeriodicidadTarea): String = value.name

    @FromJson
    fun fromJsonToPeriodicidadTarea(value: String): edu.ucne.doers.data.local.model.PeriodicidadTarea =
        edu.ucne.doers.data.local.model.PeriodicidadTarea.valueOf(value.uppercase())
}