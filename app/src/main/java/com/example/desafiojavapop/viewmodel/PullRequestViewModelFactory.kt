package com.example.desafiojavapop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.desafiojavapop.repository.HomeRepository
import com.example.desafiojavapop.usecase.FetchPullRequestsUseCase

class PullRequestViewModelFactory(
    private val fetchPullRequestsUseCase: FetchPullRequestsUseCase,
    private val homeRepository: HomeRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PullRequestViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PullRequestViewModel(fetchPullRequestsUseCase, homeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
