package com.example.desafiojavapop.di

import com.example.desafiojavapop.di.modules.apiModule
import com.example.desafiojavapop.di.modules.databaseModule
import com.example.desafiojavapop.di.modules.networkModule
import com.example.desafiojavapop.di.modules.repositoriesModule
import com.example.desafiojavapop.di.modules.useCaseModule
import com.example.desafiojavapop.di.modules.viewModelModule

val appModules = listOf(
    apiModule,
    databaseModule,
    networkModule,
    useCaseModule,
    repositoriesModule,
    viewModelModule
)