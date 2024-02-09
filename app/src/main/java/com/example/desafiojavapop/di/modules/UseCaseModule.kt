package com.example.desafiojavapop.di.modules

import com.example.desafiojavapop.domain.usecase.FetchPullRequestsUseCase
import com.example.desafiojavapop.domain.usecase.HomeFetchRepositoriesUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory {
        FetchPullRequestsUseCase(repositoriesModule = get())
    }

    factory {
        HomeFetchRepositoriesUseCase(repositoriesModule = get())
    }
}
