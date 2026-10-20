package com.example.cinemaster.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies WHERE status = :status ORDER BY addedAt DESC")
    fun getByStatus(status: MovieStatus): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE id = :id")
    fun getById(id: Int): Flow<Movie?>

    @Query("SELECT id FROM movies")
    fun getAllIds(): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(movie: Movie)

    @Update
    suspend fun update(movie: Movie)

    @Delete
    suspend fun delete(movie: Movie)
}