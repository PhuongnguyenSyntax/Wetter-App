package com.syntax.flama.wetterapp.di

import android.content.Context
import androidx.room.Room
import com.syntax.flama.wetterapp.data.database.DataBase
import com.syntax.flama.wetterapp.data.database.WeatherDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): DataBase {
        return Room.databaseBuilder(
            appContext,
            DataBase::class.java,
            "weatherDB"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideChannelDao(db: DataBase): WeatherDAO {
        return db.weatherDao
    }
}