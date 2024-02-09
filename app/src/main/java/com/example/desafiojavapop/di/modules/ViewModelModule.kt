package com.example.desafiojavapop.di.modules

import com.example.desafiojavapop.presenter.viewmodel.HomeViewModel
import com.example.desafiojavapop.presenter.viewmodel.PullRequestViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        HomeViewModel(fetchRepositoriesUseCase = get())
    }

    viewModel {
        PullRequestViewModel(fetchPullRequestsUseCase = get(),
            repositoriesModule = get())
    }
}
