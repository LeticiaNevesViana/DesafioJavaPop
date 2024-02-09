package com.example.desafiojavapop.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeModel(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val repo: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("owner")
    val owner: UserModel?,

    @SerializedName("stargazers_count")
    val starsCount: Int?,

    @SerializedName("forks_count")
    val forksCount: Int?,

    @SerializedName("full_name")
    val fullName: String?,

    var pageNumber: Int?,
) : Parcelable



