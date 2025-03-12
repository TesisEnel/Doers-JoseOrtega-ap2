package edu.ucne.doers.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.ucne.doers.data.local.entity.Hijo
import edu.ucne.doers.data.local.entity.TransaccionesHijo
import edu.ucne.doers.data.local.entity.Padre
import edu.ucne.doers.data.local.entity.Recompensa
import edu.ucne.doers.data.local.entity.Sala
import edu.ucne.doers.data.local.entity.Tarea
import edu.ucne.doers.data.local.entity.Canjeo
import edu.ucne.doers.data.local.entity.TareasHijos

@Database(
    entities = [
        Padre::class,
        Sala::class,
        Hijo::class,
        Tarea::class,
        TareasHijos::class,
        Recompensa::class,
        Canjeo::class,
        TransaccionesHijo::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class DoersDb : RoomDatabase() {

}