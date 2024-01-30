package com.example.desafiojavapop.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
data class PullRequestModel (
    val id: Int,
    val title: String?,
    @SerializedName("created_at")
    var createdAt: String?,
    val body: String?,
    @SerializedName("html_url")
    val htmlUrl: String?,
    val fullName: String?,
    val user: UserModel?
) : Parcelable {

    fun getCreatedAtDateString(): String {
    if (createdAt?.isEmpty() == true) {
        return ""
    }
    val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
    val date: Date? = createdAt?.let { originalFormat.parse(it) }

    val brazilFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    return date?.let { brazilFormat.format(it) } ?: ""
}
}

