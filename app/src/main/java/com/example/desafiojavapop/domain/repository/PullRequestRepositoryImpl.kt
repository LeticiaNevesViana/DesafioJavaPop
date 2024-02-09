package com.example.desafiojavapop.repository


import com.example.desafiojavapop.database.AppDatabase
import com.example.desafiojavapop.database.PullRequestEntity
import com.example.desafiojavapop.model.PullRequestModel
import com.example.desafiojavapop.model.UserModel
import com.example.desafiojavapop.rest.ApiService
import com.example.desafiojavapop.util.NetworkUtils
import com.example.desafiojavapop.util.ResultWrapper
import retrofit2.Response

class PullRequestRepositoryImpl(
    private val apiService: ApiService,
    private val database: AppDatabase,
    private val networkUtils: NetworkUtils
) : PullRequestRepository {

    override suspend fun getPullRequests(login: String, repoName: String): ResultWrapper<List<PullRequestModel>> {
        return if (networkUtils.isNetworkAvailable()) {
            try {
                val response = apiService.getPullRequestsFromApi(login, repoName)
                if (response.isSuccessful) {
                    val pullRequests = response.body() ?: emptyList()
                    savePullRequestsToDb(pullRequests)
                    ResultWrapper.Success(pullRequests)
                } else {
                    ResultWrapper.Failure(Exception("Erro na API"), response.errorBody()?.string(), response.code())
                }
            } catch (e: Exception) {
                ResultWrapper.Failure(e, e.message, null)
            }
        } else {
            ResultWrapper.NetworkError
        }
    }

    override suspend fun getPullRequestsFromApi(login: String, repoName: String): Response<List<PullRequestModel>> {
        return apiService.getPullRequestsFromApi(login, repoName)
    }

    override suspend fun getPullRequestsFromDb(login: String, repoName: String): List<PullRequestModel> {
        return database.pullrequestsDao().getPullRequests("$login/$repoName").map { it.toModel() }
    }

    override suspend fun savePullRequestsToDb(pullRequests: List<PullRequestModel>) {
        val entities = pullRequests.map { it.toEntity() }
        database.pullrequestsDao().insertAll(entities)
    }

    private fun PullRequestModel.toEntity(): PullRequestEntity {
        return PullRequestEntity(
            id = this.id,
            title = this.title,
            createdAt = this.createdAt,
            body = this.body,
            htmlUrl = this.htmlUrl,
            fullName = this.fullName,
            userLogin = this.user?.login,
            userAvatarUrl = this.user?.avatarUrl
        )
    }

    private fun PullRequestEntity.toModel(): PullRequestModel {
        return PullRequestModel(
            id = this.id,
            title = this.title ?: "",
            createdAt = this.createdAt ?: "",
            body = this.body ?: "",
            htmlUrl = this.htmlUrl ?: "",
            fullName = this.fullName ?: "",
            user = UserModel(this.userLogin ?: "", this.userAvatarUrl ?: "")
        )
    }
}
