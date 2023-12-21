package com.example.desafiojavapop.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "pull_requests")

data class PullRequestEntity (
    @PrimaryKey
    var id: Int,
    val title: String,
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    val body: String,
    @ColumnInfo(name = "html_url")
    val htmlUrl: String,
    @ColumnInfo(name = "full_name")
    val fullName: String?,
    @ColumnInfo(name = "user_login")
    val userLogin: String?
    )

