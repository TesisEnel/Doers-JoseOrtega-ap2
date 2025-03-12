package edu.ucne.doers.data.local.database

import androidx.room.TypeConverter
import edu.ucne.doers.data.local.model.EstadoRecompensa
import edu.ucne.doers.data.local.model.EstadoTarea
import edu.ucne.doers.data.local.model.EstadoTareaHijo
import edu.ucne.doers.data.local.model.TipoTransaccion
import java.util.Date

class Converters {
    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.time

    @TypeConverter
    fun toDate(timestamp: Long?): Date? = timestamp?.let { Date(it) }

    @TypeConverter
    fun fromEstadoTarea(value: EstadoTarea): String = value.name

    @TypeConverter
    fun toEstadoTarea(value: String): EstadoTarea = EstadoTarea.valueOf(value)

    @TypeConverter
    fun fromEstadoTareaHijo(value: EstadoTareaHijo): String = value.name

    @TypeConverter
    fun toEstadoTareaHijo(value: String): EstadoTareaHijo = EstadoTareaHijo.valueOf(value)

    @TypeConverter
    fun fromTipoTransaccion(value: TipoTransaccion): String = value.name

    @TypeConverter
    fun toTipoTransaccion(value: String): TipoTransaccion = TipoTransaccion.valueOf(value)

    @TypeConverter
    fun fromEstadoRecompensa(value: EstadoRecompensa): String = value.name

    @TypeConverter
    fun toEstadoRecompensa(value: String): EstadoRecompensa = EstadoRecompensa.valueOf(value)
}