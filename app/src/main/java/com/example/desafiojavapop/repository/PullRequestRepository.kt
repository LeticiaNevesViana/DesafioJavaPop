package com.example.desafiojavapop.repository

import com.example.desafiojavapop.model.PullRequestModel
import com.example.desafiojavapop.util.ResultWrapper

interface PullRequestRepository {
    suspend fun getPullRequestsFromApi(login: String, repository: String): ResultWrapper<List<PullRequestModel>>
}
