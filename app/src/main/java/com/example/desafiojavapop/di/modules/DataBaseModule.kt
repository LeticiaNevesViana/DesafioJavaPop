package com.example.desafiojavapop.di.modules

import androidx.room.Room
import com.example.desafiojavapop.data.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "desafio_java_pop_data_base"
        ).fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().repositoriesDao() }

    single { get<AppDatabase>().pullrequestsDao() }
}

