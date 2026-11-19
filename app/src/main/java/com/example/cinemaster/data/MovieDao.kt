package com.example.cinemaster.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies WHERE userId = :userId AND status = :status ORDER BY addedAt DESC")
    fun getByStatus(userId: Int, status: MovieStatus): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE id = :id AND userId = :userId LIMIT 1")
    fun getById(id: Int, userId: Int): Flow<Movie?>

    @Query("SELECT id FROM movies WHERE userId = :userId")
    fun getAllIds(userId: Int): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: Movie)

    @Update
    suspend fun update(movie: Movie)

    @Delete
    suspend fun delete(movie: Movie)
}
