package com.asistlab.tripnotes.di

import android.app.Application
import android.content.Context
import com.asistlab.tripnotes.data.AppDatabase
import com.asistlab.tripnotes.data.TripDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

/**
 * @author EpicDima
 */
@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideTripDao(database: AppDatabase): TripDao {
        return database.tripDao()
    }
}