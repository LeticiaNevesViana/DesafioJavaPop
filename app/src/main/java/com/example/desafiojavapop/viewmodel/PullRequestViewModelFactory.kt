package com.example.desafiojavapop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.desafiojavapop.repository.HomeRepository
import com.example.desafiojavapop.repository.PullRequestRepository
import com.example.desafiojavapop.usecase.FetchPullRequestsUseCase

class PullRequestViewModelFactory(
    private val fetchPullRequestsUseCase: FetchPullRequestsUseCase,
    private val homeRepository: HomeRepository // Adicionando o HomeRepository como um parâmetro
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PullRequestViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Passando o HomeRepository para a PullRequestViewModel
            return PullRequestViewModel(fetchPullRequestsUseCase, homeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
