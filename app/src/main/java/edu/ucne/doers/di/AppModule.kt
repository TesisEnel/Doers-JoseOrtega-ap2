package edu.ucne.doers.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.ucne.doers.data.local.database.DoersDb
import edu.ucne.doers.data.remote.DoersApi
import edu.ucne.doers.presentation.sign_in.GoogleAuthUiClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    const val BASE_URL = "https://doers-app-2025-f3aketd8gbhbggd6.eastus2-01.azurewebsites.net"

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
    @Singleton
    fun provideSignInClient(
        @ApplicationContext context: Context
    ): SignInClient {
        return Identity.getSignInClient(context)
    }

    @Provides
    @Singleton
    fun provideGoogleAuthUiClient(
        @ApplicationContext context: Context,
        signInClient: SignInClient
    ): GoogleAuthUiClient {
        return GoogleAuthUiClient(
            context = context,
            oneTapClient = signInClient,
            sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        )
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideDoersApi(moshi: Moshi): DoersApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(DoersApi::class.java)
    }
}