package com.example.desafiojavapop.di.modules

import com.example.desafiojavapop.domain.repository.HomeRepository
import com.example.desafiojavapop.domain.repository.HomeRepositoryImpl
import com.example.desafiojavapop.domain.repository.PullRequestRepository
import com.example.desafiojavapop.domain.repository.PullRequestRepositoryImpl
import org.koin.dsl.module


val repositoriesModule = module {

    single<HomeRepository> { HomeRepositoryImpl(
        apiModule = get(), databaseModule = get(), networkModule = get()) }

    single<PullRequestRepository> { PullRequestRepositoryImpl(
        apiModule = get(), databaseModule = get(), networkModule = get()) }
}
