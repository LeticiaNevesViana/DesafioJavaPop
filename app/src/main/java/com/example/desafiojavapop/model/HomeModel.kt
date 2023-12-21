package com.example.desafiojavapop.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class HomeModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val repo: String?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("owner")
    val owner: UserModel,
    @SerializedName("stargazers_count")
    val stars: Int?,
    @SerializedName("forks_count")
    val forks: Int?,
    @SerializedName("full_name")
    val fullName: String?,
    @SerializedName("page_number")
    var pageNumber: Int?

): Serializable


