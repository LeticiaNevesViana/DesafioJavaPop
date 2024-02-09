package com.example.desafiojavapop.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HomeEntity::class, PullRequestEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repositoriesDao(): HomeDao
    abstract fun pullrequestsDao(): PullRequestDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "desafio_java_pop_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}


