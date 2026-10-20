package com.example.cinemaster.data

import com.example.cinemaster.BuildConfig
import kotlinx.coroutines.flow.Flow

class MovieRepository(
    private val dao: MovieDao,
    private val api: TmdbApi
) {
    fun moviesByStatus(status: MovieStatus): Flow<List<Movie>> = dao.getByStatus(status)
    fun movie(id: Int): Flow<Movie?> = dao.getById(id)
    fun savedIds(): Flow<List<Int>> = dao.getAllIds()
    suspend fun add(movie: Movie) = dao.insert(movie)
    suspend fun update(movie: Movie) = dao.update(movie)
    suspend fun delete(movie: Movie) = dao.delete(movie)

    suspend fun search(query: String): List<Movie> =
        api.searchMovies(BuildConfig.TMDB_API_KEY, query).results.map { it.toMovie() }
}