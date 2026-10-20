package com.example.cinemaster.data

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

data class SearchResponse(val results: List<MovieDto>)

data class MovieDto(
    val id: Int,
    val title: String?,
    @SerializedName("poster_path") val posterPath: String?,
    val overview: String?,
    @SerializedName("release_date") val releaseDate: String?
)

fun MovieDto.toMovie() = Movie(
    id = id,
    title = title ?: "Không có tên",
    posterPath = posterPath,
    overview = overview ?: ""
)

interface TmdbApi {
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String = "vi-VN"
    ): SearchResponse
}