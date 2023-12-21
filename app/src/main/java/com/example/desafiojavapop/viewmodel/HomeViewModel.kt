package com.example.desafiojavapop.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.desafiojavapop.model.HomeModel
import com.example.desafiojavapop.repository.HomeRepository
import com.example.desafiojavapop.repository.HomeRepositoryImpl
import com.example.desafiojavapop.usecase.HomeFetchRepositoriesUseCase
import com.example.desafiojavapop.util.ResultWrapper
import kotlinx.coroutines.launch

class HomeViewModel(
    private val fetchRepositoriesUseCase: HomeFetchRepositoriesUseCase
) : ViewModel() {
    private var currentPage = 1
    val isLoading = MutableLiveData<Boolean>().apply { value = false }
    private val _repoList = MutableLiveData<ResultWrapper<List<HomeModel>>>()
    val repoList: LiveData<ResultWrapper<List<HomeModel>>> get() = _repoList

    fun fetchRepositories(query: String, sort: String) {
        viewModelScope.launch {
            isLoading.value = true
            val result = fetchRepositoriesUseCase(query, sort, currentPage)
            _repoList.postValue(result)
            isLoading.value = false
        }
    }

    fun loadNextPage(query: String, sort: String) {
        if (isLoading.value == true) return

        isLoading.value = true
        currentPage++
        viewModelScope.launch {
            val result = fetchRepositoriesUseCase(query, sort, currentPage)
            _repoList.postValue(result)
            isLoading.value = false
        }
    }
}
