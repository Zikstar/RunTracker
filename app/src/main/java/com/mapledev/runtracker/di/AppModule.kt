package com.mapledev.runtracker.di

import android.content.Context
import androidx.room.Room
import com.mapledev.runtracker.db.RunDatabase
import com.mapledev.runtracker.utils.Constants.RUNNING_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRunDatabase(
        @ApplicationContext context: Context): RunDatabase = Room.databaseBuilder(
        context,
        RunDatabase::class.java,
        RUNNING_DATABASE_NAME
    ).build()

    @Provides
    @Singleton
    fun provideRunDao(db: RunDatabase) = db.getRunDao()
}