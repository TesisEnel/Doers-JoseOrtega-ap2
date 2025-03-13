package edu.ucne.doers.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.ucne.doers.data.local.entity.CanjeoEntity
import edu.ucne.doers.data.local.entity.HijoEntity
import edu.ucne.doers.data.local.entity.PadreEntity
import edu.ucne.doers.data.local.entity.RecompensaEntity
import edu.ucne.doers.data.local.entity.SolicitudRecompensa
import edu.ucne.doers.data.local.entity.TareaEntity
import edu.ucne.doers.data.local.entity.TareaHijo
import edu.ucne.doers.data.local.entity.TransaccionHijo

@Database(
    entities = [
        PadreEntity::class,
        HijoEntity::class,
        TareaEntity::class,
        TareaHijo::class,
        RecompensaEntity::class,
        CanjeoEntity::class,
        TransaccionHijo::class,
        SolicitudRecompensa::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class DoersDb : RoomDatabase() {

}