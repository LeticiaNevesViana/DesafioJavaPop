package com.example.desafiojavapop.repository

import com.example.desafiojavapop.model.HomeModel
import com.example.desafiojavapop.model.HomeResponse
import com.example.desafiojavapop.util.ResultWrapper
import retrofit2.Response

interface HomeRepository {
    suspend fun getRepositories(query: String, sort: String, page: Int): ResultWrapper<List<HomeModel>>
    suspend fun getRepositoriesFromApi(query: String, sort: String, page: Int): Response<HomeResponse>
    suspend fun getRepositoriesFromDb(query: String, page: Int): List<HomeModel>
    suspend fun saveRepositoriesToDb(repositories: List<HomeModel>, query: String, page: Int)
    suspend fun getRepositoryById(repoId: Int): HomeModel



}

