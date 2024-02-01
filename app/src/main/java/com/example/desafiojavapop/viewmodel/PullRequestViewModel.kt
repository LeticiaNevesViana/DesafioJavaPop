package com.example.desafiojavapop.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.desafiojavapop.model.PullRequestModel
import com.example.desafiojavapop.repository.HomeRepository
import com.example.desafiojavapop.usecase.FetchPullRequestsUseCase
import com.example.desafiojavapop.util.ResultWrapper
import kotlinx.coroutines.launch

class PullRequestViewModel(
    private val fetchPullRequestsUseCase: FetchPullRequestsUseCase,
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _pullRequests = MutableLiveData<ResultWrapper<List<PullRequestModel>>>()
    val pullRequests: LiveData<ResultWrapper<List<PullRequestModel>>> = _pullRequests

    fun loadPullRequests(login: String, repoName: String) {
        viewModelScope.launch {
            val result = fetchPullRequestsUseCase(login, repoName) // Busca os Pull Requests
            _pullRequests.value = result // Atualiza o LiveData com o resultado
        }
    }

    // Método para carregar detalhes do repositório do banco de dados e, em seguida, carregar os Pull Requests
    fun loadRepositoryDetailsFromDb(repoId: Int) {
        viewModelScope.launch {
            val repoDetails = homeRepository.getRepositoryById(repoId) // Busca os detalhes do repositório pelo ID
            repoDetails.owner?.login?.let { login ->
                repoDetails.repo?.let { repoName ->
                    loadPullRequests(login, repoName)
                }
            }
        }
    }
}
