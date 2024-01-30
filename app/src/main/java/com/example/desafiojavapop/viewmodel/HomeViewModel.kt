package com.example.desafiojavapop.viewmodel

import android.util.Log
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

    private val _currentRepoList = MutableLiveData<List<HomeModel>>()
    val currentRepoList: LiveData<List<HomeModel>> = _currentRepoList

    private val _refreshComplete = MutableLiveData<Boolean>()
    val refreshComplete: LiveData<Boolean> = _refreshComplete

    private var isRefreshing = false

    private var isLastPage = false
        set(value) {
            field = value
            isLastPageLiveData.postValue(value)
        }

    val isLoading = MutableLiveData<Boolean>().apply { value = false }

    val isLoadingMore = MutableLiveData<Boolean>().apply { value = false }

    private val _repoList = MutableLiveData<ResultWrapper<List<HomeModel>>>()
    val repoList: LiveData<ResultWrapper<List<HomeModel>>> get() = _repoList

    val isLastPageLiveData = MutableLiveData<Boolean>()

    fun fetchRepositories(query: String, sort: String, isRefresh: Boolean = false) {
        if (isRefresh) {
            isRefreshing = true
            resetForRefresh()
            fetchRepositoryData(query, sort)
        } else if (!isLastPage && !isRefreshing) {
            fetchMoreRepositoryData(query, sort)
        }
    }

    private fun resetForRefresh() {
        currentPage = 1
        isLastPage = false
        _currentRepoList.postValue(emptyList())
    }

    private fun fetchRepositoryData(query: String, sort: String) {
        viewModelScope.launch {
            isLoading.value = true
            val result = fetchRepositoriesUseCase(query, sort, currentPage)
            handleRepositoryResult(result)
            isLoading.value = false
            _refreshComplete.postValue(true)
        }
    }

    // Método separado para carregar mais dados.
    private fun fetchMoreRepositoryData(query: String, sort: String) {
        viewModelScope.launch {
            isLoadingMore.value = true
            val result = fetchRepositoriesUseCase(query, sort, currentPage)
            handleRepositoryResult(result)
            isLoadingMore.value = false
        }
    }

    private fun handleRepositoryResult(result: ResultWrapper<List<HomeModel>>) {
        when (result) {
            is ResultWrapper.Success -> updateRepositoryList(result.data)
            is ResultWrapper.Failure -> _repoList.postValue(result)
            else -> Log.w("HomeViewModel", "Resposta inesperada ou desconhecida da API.")
        }
    }

    private fun updateRepositoryList(newData: List<HomeModel>) {
        val updatedList = if (currentPage == 1) newData else _currentRepoList.value.orEmpty() + newData
        _currentRepoList.postValue(updatedList)
        if (newData.isEmpty()) isLastPage = true
        if (isRefreshing) {
            isRefreshing = false
        }
    }

    fun loadNextPage(query: String, sort: String) {
        if (isLoadingMore.value == true || isLastPage ) return
        currentPage++
        fetchMoreRepositoryData(query, sort)
    }
}
