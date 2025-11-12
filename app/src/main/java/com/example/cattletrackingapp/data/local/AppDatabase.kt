package com.example.cattletrackingapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cattletrackingapp.data.local.dao.*
import com.example.cattletrackingapp.data.local.entity.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Database(
    entities = [CowEntity::class, CalfEntity::class, BullEntity::class, FarmerEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cowDao(): CowDao
    abstract fun calfDao(): CalfDao
    abstract fun bullDao(): BullDao
    abstract fun farmerDao(): FarmerDao
}


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "farm_db").build()

    @Provides
    fun provideCowDao(db: AppDatabase): CowDao = db.cowDao()

    @Provides
    fun provideCalfDao(db: AppDatabase): CalfDao = db.calfDao()

    @Provides
    fun provideBullDao(db: AppDatabase): BullDao = db.bullDao()

    @Provides
    fun provideFarmerDao(db: AppDatabase): FarmerDao = db.farmerDao()
}
