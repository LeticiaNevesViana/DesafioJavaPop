package com.example.desafiojavapop.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HomeDao {
    @Query("SELECT * FROM repositories WHERE `query` = :query AND page = :page")
    suspend fun getRepositories(query: String, page: Int): List<HomeEntity>

    @Query("SELECT * FROM repositories WHERE id = :repoId")
    suspend fun getRepositoryById(repoId: Int): HomeEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repositories: List<HomeEntity>)


}
