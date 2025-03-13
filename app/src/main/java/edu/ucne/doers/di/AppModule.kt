package edu.ucne.doers.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.ucne.doers.data.local.database.DoersDb
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    const val BASE_URL = ""

    //Moshi
    @Provides
    @Singleton
    fun providesMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(Adapter())
            .build()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context) =
        Room.databaseBuilder(
            appContext,
            DoersDb::class.java,
            "Doers.db"
        ).fallbackToDestructiveMigration()
            .build()

    //Firebase
    @Provides
    @Singleton
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    fun providePadreDao(doersDb: DoersDb) = doersDb.padreDao()
    @Provides
    fun provideHijoDao(doersDb: DoersDb) = doersDb.hijoDao()
    @Provides
    fun provideTareaDao(doersDb: DoersDb) = doersDb.tareaDao()
    @Provides
    fun provideTareaHijoDao(doersDb: DoersDb) = doersDb.tareaHijoDao()
    @Provides
    fun provideRecompensaDao(doersDb: DoersDb) = doersDb.recompensaDao()
    @Provides
    fun provideCanjeoDao(doersDb: DoersDb) = doersDb.canjeoDao()
    @Provides
    fun provideTransaccionHijoDao(doersDb: DoersDb) = doersDb.transaccionHijoDao()
    @Provides
    fun provideSolicitudRecompensaDao(doersDb: DoersDb) = doersDb.solicitudRecompensaDao()
}