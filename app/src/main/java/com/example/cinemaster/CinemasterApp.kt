package com.example.cinemaster

import android.app.Application
import com.example.cinemaster.data.AppDatabase
import com.example.cinemaster.data.MovieRepository
import com.example.cinemaster.data.TmdbApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CinemasterApp : Application() {

    private val api: TmdbApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApi::class.java)
    }

    val repository by lazy {
        MovieRepository(AppDatabase.get(this).movieDao(), api)
    }
}