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
    private val fetchPullRequestsUseCase: FetchPullRequestsUseCase, // Injeta a dependência para buscar Pull Requests
    private val homeRepository: HomeRepository // Injeta a dependência do repositório para acessar dados do banco de dados
) : ViewModel() {

    // MutableLiveData para armazenar o estado dos Pull Requests; exposto como LiveData
    private val _pullRequests = MutableLiveData<ResultWrapper<List<PullRequestModel>>>()
    val pullRequests: LiveData<ResultWrapper<List<PullRequestModel>>> = _pullRequests

    // Método para carregar Pull Requests de um repositório específico
    fun loadPullRequests(login: String, repoName: String) {
        viewModelScope.launch { // Inicia uma coroutine no escopo do ViewModel
            val result = fetchPullRequestsUseCase(login, repoName) // Busca os Pull Requests
            _pullRequests.value = result // Atualiza o LiveData com o resultado
        }
    }

    // Método para carregar detalhes do repositório do banco de dados e, em seguida, carregar os Pull Requests
    fun loadRepositoryDetailsFromDb(repoId: Int) {
        viewModelScope.launch { // Inicia uma coroutine no escopo do ViewModel
            val repoDetails = homeRepository.getRepositoryById(repoId) // Busca os detalhes do repositório pelo ID
            // Se o login do proprietário e o nome do repositório não forem nulos, carrega os Pull Requests
            repoDetails.owner?.login?.let { login ->
                repoDetails.repo?.let { repoName ->
                    loadPullRequests(login, repoName) // Chama o método para carregar os Pull Requests
                }
            }
        }
    }
}
