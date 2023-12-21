package com.example.desafiojavapop.repository

import com.example.desafiojavapop.database.AppDatabase
import com.example.desafiojavapop.database.HomeEntity
import com.example.desafiojavapop.model.HomeModel
import com.example.desafiojavapop.model.HomeResponse
import com.example.desafiojavapop.model.UserModel
import com.example.desafiojavapop.rest.ApiService
import retrofit2.Response

class HomeRepositoryImpl(
    private val apiService: ApiService,
    private val database: AppDatabase
): HomeRepository {
    override suspend fun getRepositoriesFromApi(query: String, sort: String, page: Int): Response<HomeResponse> {
        return apiService.getRepositoriesFromApi(query, sort, page)
    }

    override suspend fun getRepositoriesFromDb(query: String, page: Int): List<HomeModel> {
        return database.repositoriesDao().getRepositories(query, page).map { it.toHomeModel() }
    }

    override suspend fun saveRepositoriesToDb(repositories: List<HomeModel>, query: String, page: Int) {
        database.repositoriesDao().insertAll(repositories.map { it.toEntity(query, page) })
    }

    private fun HomeEntity.toHomeModel(): HomeModel {
        return HomeModel(
            id = this.id,
            repo = this.repo,
            description = this.description,
            owner = UserModel(this.ownerLogin, this.ownerAvatarUrl),
            stars = this.stars,
            forks = this.forks,
            fullName = this.fullName,
            pageNumber = this.page
        )
    }

    private fun HomeModel.toEntity(query: String, page: Int): HomeEntity {
        return HomeEntity(
            id = this.id,
            repo = this.repo ?: "",
            description = this.description ?: "",
            ownerLogin = this.owner?.login ?: "",
            ownerAvatarUrl = this.owner?.avatarUrl ?: "",
            stars = this.stars ?: 0,
            forks = this.forks ?: 0,
            fullName = this.fullName ?: "",
            query = query,
            page = page
        )
    }
}
