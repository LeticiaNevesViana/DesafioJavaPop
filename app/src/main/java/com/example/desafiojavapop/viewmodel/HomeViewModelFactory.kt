package com.example.desafiojavapop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.desafiojavapop.usecase.HomeFetchRepositoriesUseCase

class HomeViewModelFactory(private val fetchRepositoriesUseCase: HomeFetchRepositoriesUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(fetchRepositoriesUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
