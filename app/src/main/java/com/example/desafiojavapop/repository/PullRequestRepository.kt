package com.example.desafiojavapop.repository

import com.example.desafiojavapop.model.PullRequestModel
import com.example.desafiojavapop.util.ResultWrapper
import retrofit2.Response

interface PullRequestRepository {
    suspend fun getPullRequests(login: String, repoName: String): ResultWrapper<List<PullRequestModel>>

    suspend fun getPullRequestsFromApi(login: String, repoName: String): Response<List<PullRequestModel>>

    suspend fun getPullRequestsFromDb(login: String, repoName: String): List<PullRequestModel>
    suspend fun savePullRequestsToDb(pullRequests: List<PullRequestModel>)

}

