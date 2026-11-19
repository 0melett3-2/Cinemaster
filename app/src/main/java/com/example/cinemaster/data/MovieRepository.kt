package com.example.cinemaster.data

import com.example.cinemaster.BuildConfig
import kotlinx.coroutines.flow.Flow

class MovieRepository(
    private val dao: MovieDao,
    private val api: TmdbApi,
    private val authRepo: AuthRepository
) {
    private val currentUserId: Int
        get() = authRepo.currentUserId

    fun moviesByStatus(status: MovieStatus): Flow<List<Movie>> =
        dao.getByStatus(currentUserId, status)

    fun movie(id: Int): Flow<Movie?> =
        dao.getById(id, currentUserId)

    fun savedIds(): Flow<List<Int>> =
        dao.getAllIds(currentUserId)

    suspend fun add(movie: Movie) =
        dao.insert(movie.copy(userId = currentUserId))

    suspend fun update(movie: Movie) =
        dao.update(movie.copy(userId = currentUserId))

    suspend fun delete(movie: Movie) =
        dao.delete(movie)

    suspend fun search(query: String): List<Movie> =
        api.searchMovies(BuildConfig.TMDB_API_KEY, query).results.map {
            it.toMovie(userId = currentUserId)
        }
}
