package com.example.cinemaster

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cinemaster.data.Movie
import com.example.cinemaster.data.MovieRepository
import com.example.cinemaster.data.MovieStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repo: MovieRepository,
    movieId: Int
) : ViewModel() {

    val movie: StateFlow<Movie?> = repo.movie(movieId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setStatus(movie: Movie, status: MovieStatus) =
        viewModelScope.launch { repo.update(movie.copy(status = status)) }

    fun saveReview(movie: Movie, rating: Float?, note: String) =
        viewModelScope.launch { repo.update(movie.copy(rating = rating, note = note)) }

    fun delete(movie: Movie) = viewModelScope.launch { repo.delete(movie) }

    companion object {
        fun factory(movieId: Int) = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as CinemasterApp
                DetailViewModel(app.repository, movieId)
            }
        }
    }
}