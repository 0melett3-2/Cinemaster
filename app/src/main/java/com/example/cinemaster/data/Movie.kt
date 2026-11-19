package com.example.cinemaster.data

import androidx.room.Entity

enum class MovieStatus { WANT, WATCHING, WATCHED }

@Entity(
    tableName = "movies",
    primaryKeys = ["id", "userId"] // Cùng 1 phim TMDB nhưng thuộc 2 user khác nhau sẽ là 2 bản ghi riêng
)
data class Movie(
    val id: Int,                        // TMDB movie ID
    val userId: Int = 0,                // ID người dùng (-1: Khách, > 0: Thành viên đăng ký)
    val title: String,
    val posterPath: String? = null,
    val overview: String = "",
    val status: MovieStatus = MovieStatus.WANT,
    val rating: Float? = null,          // chấm điểm khi đã xem
    val note: String = "",
    val addedAt: Long = System.currentTimeMillis()
)
