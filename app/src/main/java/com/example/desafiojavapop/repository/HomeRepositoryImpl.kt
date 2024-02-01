package com.example.desafiojavapop.repository

import android.util.Log
import com.example.desafiojavapop.database.AppDatabase
import com.example.desafiojavapop.database.HomeEntity
import com.example.desafiojavapop.model.HomeModel
import com.example.desafiojavapop.model.HomeResponse
import com.example.desafiojavapop.model.UserModel
import com.example.desafiojavapop.rest.ApiService
import com.example.desafiojavapop.util.NetworkUtils
import com.example.desafiojavapop.util.ResultWrapper
import retrofit2.Response

class HomeRepositoryImpl(
    private val apiService: ApiService,
    private val database: AppDatabase,
    private val networkUtils: NetworkUtils
) : HomeRepository {

    override suspend fun getRepositories(query: String, sort: String, page: Int): ResultWrapper<List<HomeModel>> {
        return if (networkUtils.isNetworkAvailable()) {
            try {
                val response = apiService.getRepositoriesFromApi(query, sort, page)
                if (response.isSuccessful) {
                    val repoList = response.body()?.items ?: emptyList()
                    saveRepositoriesToDb(repoList, query, page)
                    ResultWrapper.Success(repoList)
                } else {
                    Log.e("API Error", "Code: ${response.code()} Message: ${response.message()}")
                    fetchFromCache(query, page)
                }
            } catch (e: Exception) {
                Log.e("API Exception", e.message ?: "Unknown error")
                fetchFromCache(query, page)
            }
        } else {
            ResultWrapper.NetworkError
        }
    }

    override suspend fun getRepositoriesFromApi(query: String, sort: String, page: Int): Response<HomeResponse> {
        return apiService.getRepositoriesFromApi(query, sort, page)
    }

    override suspend fun getRepositoriesFromDb(query: String, page: Int): List<HomeModel> {
        return database.repositoriesDao().getRepositories(query, page).map { it.toHomeModel() }
    }

    override suspend fun saveRepositoriesToDb(repositories: List<HomeModel>, query: String, page: Int) {
        val entities = repositories.map { it.toEntity(query, page) }
        database.repositoriesDao().insertAll(entities)
    }

    private suspend fun fetchFromCache(query: String, page: Int): ResultWrapper<List<HomeModel>> {
        val cachedRepos = getRepositoriesFromDb(query, page)
        return if (cachedRepos.isNotEmpty()) {
            ResultWrapper.Success(cachedRepos)
        } else {
            ResultWrapper.Failure(Exception("Não há dados em cache para serem exibidos."))
        }
    }

    override suspend fun getRepositoryById(repoId: Int): HomeModel {
        return database.repositoriesDao().getRepositoryById(repoId).toHomeModel()
    }

}

    private fun HomeEntity.toHomeModel(): HomeModel {
        return HomeModel(
            id = this.id,
            repo = this.repo,
            description = this.description,
            owner = UserModel(this.ownerLogin, this.ownerAvatarUrl),
            starsCount = this.stars,
            forksCount = this.forks,
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
            stars = this.starsCount ?: 0,
            forks = this.forksCount ?: 0,
            fullName = this.fullName ?: "",
            query = query,
            page = page
        )
    }
