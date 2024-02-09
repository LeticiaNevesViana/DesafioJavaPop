package com.example.desafiojavapop.rest

import com.example.desafiojavapop.model.HomeResponse
import com.example.desafiojavapop.model.PullRequestModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("search/repositories")
    suspend fun getRepositoriesFromApi(
        @Query("q") query: String,
        @Query("sort") sort: String,
        @Query("page") page: Int
    ): Response<HomeResponse>

    @GET("repos/{login}/{repository}/pulls")
    suspend fun getPullRequestsFromApi(
        @Path("login") login: String,
        @Path("repository") repository: String
    ): Response<List<PullRequestModel>>
}

