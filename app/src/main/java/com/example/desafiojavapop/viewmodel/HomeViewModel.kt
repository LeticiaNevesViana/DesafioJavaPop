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
    private val fetchRepositoriesUseCase:
    HomeFetchRepositoriesUseCase // Injeta a dependência para buscar dados dos repositórios
) : ViewModel() {

    private var currentPage = 1 // Rastreia a página atual para paginação

    // MutableLiveData interno para lista de repositórios; LiveData externo para exposição segura
    private val _currentRepoList = MutableLiveData<List<HomeModel>>()
    val currentRepoList: LiveData<List<HomeModel>> = _currentRepoList

    // Controla o estado de conclusão do refresh dos dados
    private val _refreshComplete = MutableLiveData<Boolean>()
    val refreshComplete: LiveData<Boolean> = _refreshComplete

    private var isRefreshing = false // Indica se uma operação de refresh está em andamento

    // Controla se a última página foi alcançada e notifica a UI sobre mudanças
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

    val isLastPageLiveData = MutableLiveData<Boolean>() // Exposição da última página para a UI

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
        currentPage = 1 // Reseta para a primeira página
        isLastPage = false // Reinicia a verificação da última página
        _currentRepoList.postValue(emptyList()) // Limpa a lista atual
    }

    private fun fetchRepositoryData(query: String, sort: String) {
        viewModelScope.launch {
            isLoading.value = true // Inicia o indicador de carregamento
            val result = fetchRepositoriesUseCase(query, sort, currentPage) // Busca os dados
            handleRepositoryResult(result) // Trata o resultado
            isLoading.value = false // Finaliza o indicador de carregamento
            _refreshComplete.postValue(true) // Notifica a conclusão do refresh
        }
    }

    private fun fetchMoreRepositoryData(query: String, sort: String) {
        viewModelScope.launch {
            isLoadingMore.value = true // Indicador de carregamento para carregar mais dados
            val result = fetchRepositoriesUseCase(query, sort, currentPage) // Busca mais dados
            handleRepositoryResult(result)
            isLoadingMore.value = false // Finaliza o indicador de carregamento
        }
    }

    private fun handleRepositoryResult(result: ResultWrapper<List<HomeModel>>) {
        when (result) {
            is ResultWrapper.Success -> updateRepositoryList(result.data) // Em caso de sucesso, atualiza a lista
            is ResultWrapper.Failure -> _repoList.postValue(result) // Em caso de falha, posta o resultado
            else -> Log.w("HomeViewModel", "Resposta inesperada ou desconhecida da API.")
        }
    }

    private fun updateRepositoryList(newData: List<HomeModel>) {
        val updatedList = if (currentPage == 1) newData else _currentRepoList.value.orEmpty() + newData // Atualiza ou concatena a lista
        _currentRepoList.postValue(updatedList) // Notifica a mudança de dados
        if (newData.isEmpty()) isLastPage = true // Verifica se é a última página
        if (isRefreshing) {
            isRefreshing = false // Reseta o estado de refresh
        }
    }

    fun loadNextPage(query: String, sort: String) {
        if (isLoadingMore.value == true || isLastPage) return
        currentPage++
        fetchMoreRepositoryData(query, sort)
    }
}
