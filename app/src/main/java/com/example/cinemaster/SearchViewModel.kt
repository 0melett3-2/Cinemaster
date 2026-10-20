package com.example.cinemaster

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cinemaster.data.Movie
import com.example.cinemaster.data.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SearchUiState(
    val results: List<Movie> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

class SearchViewModel(private val repo: MovieRepository) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state

    // tập hợp id các phim đã lưu trong database
    val savedIds: StateFlow<Set<Int>> = repo.savedIds()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun search(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _state.value = SearchUiState(loading = true)
            _state.value = try {
                SearchUiState(results = repo.search(query.trim()))
            } catch (e: Exception) {
                SearchUiState(error = "Không tải được dữ liệu. Kiểm tra mạng hoặc API key.")
            }
        }
    }

    fun add(movie: Movie) = viewModelScope.launch { repo.add(movie) }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as CinemasterApp
                SearchViewModel(app.repository)
            }
        }
    }
}