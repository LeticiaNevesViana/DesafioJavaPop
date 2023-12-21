package com.example.desafiojavapop.usecase

import com.example.desafiojavapop.model.HomeModel
import com.example.desafiojavapop.repository.HomeRepositoryImpl
import com.example.desafiojavapop.util.ResultWrapper

class HomeFetchRepositoriesUseCase (
    private val repository: HomeRepositoryImpl
    ) {
        suspend operator fun invoke(query: String, sort: String, page: Int): ResultWrapper<List<HomeModel>> {
            try {
                val cachedRepos = repository.getRepositoriesFromDb(query, page)
                return if (cachedRepos.isNotEmpty()) {
                    ResultWrapper.Success(cachedRepos)
                } else {
                    val response = repository.getRepositoriesFromApi(query, sort, page)
                    if (response.isSuccessful) {
                        val repoList = response.body()?.items ?: emptyList()
                        repository.saveRepositoriesToDb(repoList, query, page)
                        ResultWrapper.Success(repoList)
                    } else {
                        ResultWrapper.Failure(Exception("Erro na chamada da API"))
                    }
                }
            } catch (e: Exception) {
                return ResultWrapper.Failure(e)
            }
        }
    }

