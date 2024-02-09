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
import com.example.desafiojavapop.util.LoadType
import com.example.desafiojavapop.util.ResultWrapper
import kotlinx.coroutines.launch

class HomeViewModel(
    private val fetchRepositoriesUseCase:
    HomeFetchRepositoriesUseCase
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

    // LiveData para gerenciar estados de carregamento
    val isLoading = MutableLiveData<Boolean>().apply { value = false }
    val isLoadingMore = MutableLiveData<Boolean>().apply { value = false }

    // Armazena o resultado da busca por repositórios, seja sucesso ou falha
    private val _repoList = MutableLiveData<ResultWrapper<List<HomeModel>>>()
    val repoList: LiveData<ResultWrapper<List<HomeModel>>> get() = _repoList

    val isLastPageLiveData = MutableLiveData<Boolean>()

    fun fetchData(query: String, sort: String, loadType: LoadType) {
        viewModelScope.launch {
            startLoading(loadType)
            handleRepositoryResult(fetchRepositoriesUseCase(query, sort, currentPage))
            stopLoading(loadType)
        }
    }
    private fun startLoading(loadType: LoadType) {
        when (loadType) {
            LoadType.INITIAL, LoadType.REFRESH -> {
                isLoading.value = true
                if (loadType == LoadType.REFRESH) resetForRefresh()
            }
            LoadType.MORE -> isLoadingMore.value = true
        }
    }
    private fun stopLoading(loadType: LoadType) {
        when (loadType) {
            LoadType.INITIAL, LoadType.REFRESH -> {
                isLoading.value = false
                if (loadType == LoadType.REFRESH) _refreshComplete.postValue(true)
            }
            LoadType.MORE -> isLoadingMore.value = false
        }
        if (loadType == LoadType.MORE && !isLastPage) currentPage++
    }

    private fun resetForRefresh() {
        currentPage = 1
        isLastPage = false
        _currentRepoList.postValue(emptyList())
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
        _currentRepoList.postValue(updatedList) // Notifica a mudança de dados
        if (newData.isEmpty()) isLastPage = true // Verifica se é a última página
        if (isRefreshing) {
            isRefreshing = false
        }
    }
}
