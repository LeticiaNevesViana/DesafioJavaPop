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

    // Função para obter repositórios. Primeiro verifica se há conexão de rede.
    override suspend fun getRepositories(query: String, sort: String, page: Int): ResultWrapper<List<HomeModel>> {
        return if (networkUtils.isNetworkAvailable()) {
            // Se a rede está disponível, tenta buscar dados da API.
            try {
                val response = apiService.getRepositoriesFromApi(query, sort, page)
                if (response.isSuccessful) {
                    // Se a resposta da API for bem-sucedida, processa e retorna os dados.
                    val repoList = response.body()?.items ?: emptyList()
                    saveRepositoriesToDb(repoList, query, page) // Salva os dados no banco de dados.
                    ResultWrapper.Success(repoList)
                } else {
                    // Se a resposta da API não for bem-sucedida, tenta buscar do cache.
                    fetchFromCache(query, page)
                }
            } catch (e: Exception) {
                // Em caso de exceção, busca dados do cache.
                Log.e("getRepositories", "Error fetching from API: ${e.message}")
                fetchFromCache(query, page)
            }
        } else {
            // Se a rede não está disponível, busca diretamente do cache.
            Log.d("getRepositories", "Network is not available, fetching from cache")
            fetchFromCache(query, page)
        }
    }

    // Função para buscar dados da API.
    override suspend fun getRepositoriesFromApi(query: String, sort: String, page: Int): Response<HomeResponse> {
        return apiService.getRepositoriesFromApi(query, sort, page)
    }

    // Função para buscar dados do banco de dados.
    override suspend fun getRepositoriesFromDb(query: String, page: Int): List<HomeModel> {
        return database.repositoriesDao().getRepositories(query, page).map { it.toHomeModel() }
    }

    // Função para salvar dados no banco de dados.
    override suspend fun saveRepositoriesToDb(repositories: List<HomeModel>, query: String, page: Int) {
        val entities = repositories.map { it.toEntity(query, page) }
        database.repositoriesDao().insertAll(entities)
    }

    // Função para buscar dados do cache.
    private suspend fun fetchFromCache(query: String, page: Int): ResultWrapper<List<HomeModel>> {
        val cachedRepos = getRepositoriesFromDb(query, page)
        return if (cachedRepos.isNotEmpty()) {
            ResultWrapper.Success(cachedRepos)
        } else {
            ResultWrapper.Failure(Exception("No cached data available."))
        }
    }

    override suspend fun getRepositoryById(repoId: Int): HomeModel {
        return database.repositoriesDao().getRepositoryById(repoId).toHomeModel()
    }

    // Funções auxiliares para conversão entre entidades de banco de dados e modelos.
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
