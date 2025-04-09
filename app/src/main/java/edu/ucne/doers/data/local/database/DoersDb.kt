package edu.ucne.doers.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.ucne.doers.data.local.dao.CanjeoDao
import edu.ucne.doers.data.local.dao.HijoDao
import edu.ucne.doers.data.local.dao.PadreDao
import edu.ucne.doers.data.local.dao.RecompensaDao
import edu.ucne.doers.data.local.dao.TareaDao
import edu.ucne.doers.data.local.dao.TareaHijoDao
import edu.ucne.doers.data.local.dao.TransaccionHijoDao
import edu.ucne.doers.data.local.entity.CanjeoEntity
import edu.ucne.doers.data.local.entity.HijoEntity
import edu.ucne.doers.data.local.entity.PadreEntity
import edu.ucne.doers.data.local.entity.RecompensaEntity
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
    ],
    version = 5
)
@TypeConverters(Converters::class)
abstract class DoersDb : RoomDatabase() {
    abstract fun padreDao(): PadreDao
    abstract fun hijoDao(): HijoDao
    abstract fun tareaDao(): TareaDao
    abstract fun tareaHijoDao(): TareaHijoDao
    abstract fun recompensaDao(): RecompensaDao
    abstract fun canjeoDao(): CanjeoDao
    abstract fun transaccionHijoDao(): TransaccionHijoDao
}