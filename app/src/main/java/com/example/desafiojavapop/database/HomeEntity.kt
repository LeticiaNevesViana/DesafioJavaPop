package com.example.desafiojavapop.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repositories")
data class HomeEntity(
    @PrimaryKey var id: Int,
    val repo: String,
    val description: String,
    val ownerLogin: String,
    val ownerAvatarUrl: String,
    val stars: Int,
    val forks: Int,
    val fullName: String,
    val query: String,
    val page: Int
)
