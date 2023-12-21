package com.example.desafiojavapop.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PullRquestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pullRequests: List<PullRequestEntity>)

    @Query("SELECT * FROM pull_requests WHERE full_name = :fullName")
    suspend fun getPullRequests(fullName: String): List<PullRequestEntity>


}
