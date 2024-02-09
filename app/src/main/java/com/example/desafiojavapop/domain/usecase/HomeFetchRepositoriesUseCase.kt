package com.example.desafiojavapop.usecase

import com.example.desafiojavapop.model.HomeModel
import com.example.desafiojavapop.repository.HomeRepository
import com.example.desafiojavapop.repository.HomeRepositoryImpl
import com.example.desafiojavapop.util.ResultWrapper

class HomeFetchRepositoriesUseCase (
    private val repository: HomeRepository
) {
    suspend operator fun invoke(query: String, sort: String, page: Int): ResultWrapper<List<HomeModel>> {
        return repository.getRepositories(query, sort, page)
    }
}
