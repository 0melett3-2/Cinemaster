package com.example.cinemaster

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cinemaster.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MovieListViewModel(private val repo: MovieRepository) : ViewModel() {

    private fun listOf(status: MovieStatus) = repo.moviesByStatus(status)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wantList = listOf(MovieStatus.WANT)
    val watchingList = listOf(MovieStatus.WATCHING)
    val watchedList = listOf(MovieStatus.WATCHED)

    fun add(movie: Movie) = viewModelScope.launch { repo.add(movie) }
    fun delete(movie: Movie) = viewModelScope.launch { repo.delete(movie) }
    fun changeStatus(movie: Movie, status: MovieStatus) =
        viewModelScope.launch { repo.update(movie.copy(status = status)) }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as CinemasterApp
                MovieListViewModel(app.repository)
            }
        }
    }
}