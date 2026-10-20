package com.example.cinemaster.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MovieStatus { WANT, WATCHING, WATCHED }

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey val id: Int,            // dùng luôn id của TMDB để không bị trùng phim
    val title: String,
    val posterPath: String? = null,
    val overview: String = "",
    val status: MovieStatus = MovieStatus.WANT,
    val rating: Float? = null,          // chấm điểm khi đã xem
    val note: String = "",
    val addedAt: Long = System.currentTimeMillis()
)