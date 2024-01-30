package com.example.desafiojavapop.usecase

import com.example.desafiojavapop.model.PullRequestModel
import com.example.desafiojavapop.repository.PullRequestRepository
import com.example.desafiojavapop.util.ResultWrapper

class FetchPullRequestsUseCase(
    private val repository: PullRequestRepository) {
    suspend operator fun invoke(login: String, repoName: String): ResultWrapper<List<PullRequestModel>> {
        return repository.getPullRequests(login, repoName)
    }
}
