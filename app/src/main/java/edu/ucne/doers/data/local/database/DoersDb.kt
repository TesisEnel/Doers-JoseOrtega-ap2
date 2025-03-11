package edu.ucne.doers.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import edu.ucne.doers.data.local.entity.Cartera
import edu.ucne.doers.data.local.entity.Hijo
import edu.ucne.doers.data.local.entity.MovimientosCartera
import edu.ucne.doers.data.local.entity.Padre
import edu.ucne.doers.data.local.entity.Recompensa
import edu.ucne.doers.data.local.entity.Sala
import edu.ucne.doers.data.local.entity.Tarea
import edu.ucne.doers.data.local.entity.relation.Canjeo
import edu.ucne.doers.data.local.entity.relation.TareasHijos


@Database(
    entities = [
        Padre::class,
        Sala::class,
        Hijo::class,
        Tarea::class,
        TareasHijos::class,
        Recompensa::class,
        Canjeo::class,
        Cartera::class,
        MovimientosCartera::class
    ],
    version = 1
)
abstract class DoersDb : RoomDatabase() {

}