package com.example.desafiojavapop.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.desafiojavapop.model.PullRequestModel
import com.example.desafiojavapop.repository.HomeRepository
import com.example.desafiojavapop.repository.PullRequestRepository
import com.example.desafiojavapop.repository.PullRequestRepositoryImpl
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
            val result = fetchPullRequestsUseCase(login, repoName)
            _pullRequests.value = result
        }
    }

    fun loadRepositoryDetailsFromDb(repoId: Int) {
        viewModelScope.launch {
            val repoDetails = homeRepository.getRepositoryById(repoId)
            repoDetails.owner?.login?.let { repoDetails.repo?.let { it1 ->
                loadPullRequests(it,
                    it1
                )
            } }
        }
    }
}
