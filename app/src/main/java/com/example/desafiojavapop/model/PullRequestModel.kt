package com.example.desafiojavapop.model

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PullRequestModel (
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("created_at")
    var createdAt: String,
    @SerializedName("body")
    val body: String,
    @SerializedName("html_url")
    val htmlUrl: String,
    @SerializedName("full_name")
    val fullName: String?,
    @SerializedName("user")
    val user: UserModel?,

    ) {

    fun getCreatedAtDateString(): String {
    if (createdAt.isEmpty()) {
        return ""
    }
    val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
    val date: Date? = originalFormat.parse(createdAt)

    val brazilFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    return date?.let { brazilFormat.format(it) } ?: ""
}
}

