package edu.ucne.doers.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.ucne.doers.data.local.entity.HijoEntity
import edu.ucne.doers.data.local.entity.TransaccionHijoEntity
import edu.ucne.doers.data.local.entity.PadreEntity
import edu.ucne.doers.data.local.entity.RecompensaEntity
import edu.ucne.doers.data.local.entity.SalaEntity
import edu.ucne.doers.data.local.entity.TareaEntity
import edu.ucne.doers.data.local.entity.CanjeoEntity
import edu.ucne.doers.data.local.entity.TareaHijoEntity

@Database(
    entities = [
        PadreEntity::class,
        SalaEntity::class,
        HijoEntity::class,
        TareaEntity::class,
        TareaHijoEntity::class,
        RecompensaEntity::class,
        CanjeoEntity::class,
        TransaccionHijoEntity::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class DoersDb : RoomDatabase() {

}