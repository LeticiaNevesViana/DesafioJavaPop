package com.example.desafiojavapop.repository


import com.example.desafiojavapop.database.AppDatabase
import com.example.desafiojavapop.database.PullRequestEntity
import com.example.desafiojavapop.model.PullRequestModel
import com.example.desafiojavapop.model.UserModel
import com.example.desafiojavapop.rest.ApiService
import com.example.desafiojavapop.util.ResultWrapper

class PullRequestRepositoryImpl(
    private val apiService: ApiService,
    private val database: AppDatabase,

) : PullRequestRepository {

    override suspend fun getPullRequestsFromApi(login: String, repoName: String): ResultWrapper<List<PullRequestModel>> {
        val cachedPullRequests = database.pullrequestsDao().getPullRequests("$login/$repoName")
        if (cachedPullRequests.isNotEmpty()) {
            return ResultWrapper.Success(cachedPullRequests.map { it.toModel() })
        }

        return try {
            val response = apiService.getPullRequestsFromApi(login, repoName)
            if (response.isSuccessful) {
                val pullRequests = response.body() ?: emptyList()
                if (pullRequests.isEmpty()) {
                    return ResultWrapper.Empty
                } else {
                    database.pullrequestsDao().insertAll(pullRequests.map { pr -> pr.toEntity() })
                    return ResultWrapper.Success(pullRequests)
                }
            } else {
                ResultWrapper.Failure(RuntimeException("Erro ao buscar pull requests: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            ResultWrapper.Failure(e)
        }
    }

    private fun PullRequestModel.toEntity(): PullRequestEntity {
        return PullRequestEntity(
            id = this.id,
            title = this.title,
            createdAt = this.let {createdAt},
            body = this.body,
            htmlUrl = this.htmlUrl,
            fullName = this.fullName,
            userLogin = this.user?.login
        )
    }

    private fun PullRequestEntity.toModel(): PullRequestModel {
        return PullRequestModel(
            id = this.id,
            title = this.title,
            createdAt = this.createdAt,
            body = this.body,
            htmlUrl = this.htmlUrl,
            fullName = this.fullName,
            user = UserModel(this.userLogin ?: "", "")
        )
    }
}

