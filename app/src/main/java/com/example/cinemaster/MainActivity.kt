package com.example.cinemaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cinemaster.ui.theme.CinemasterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CinemasterTheme {
                val nav = rememberNavController()
                val app = application as CinemasterApp

                // Nếu đã đăng nhập hoặc dùng khách → vào thẳng Home
                val startDestination = if (app.authRepository.isAuthenticated) "home" else "auth"

                NavHost(navController = nav, startDestination = startDestination) {

                    composable("auth") {
                        AuthScreen(
                            onAuthenticated = {
                                nav.navigate("home") {
                                    popUpTo("auth") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("home") {
                        HomeScreen(
                            userName = app.authRepository.currentUserName,
                            isGuest = app.authRepository.isGuest,
                            onMovieClick = { id -> nav.navigate("detail/$id") },
                            onSearchClick = { nav.navigate("search") },
                            onLogout = {
                                app.authRepository.logout()
                                nav.navigate("auth") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("search") {
                        SearchScreen(onBack = { nav.popBackStack() })
                    }

                    composable(
                        route = "detail/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.IntType })
                    ) { entry ->
                        DetailScreen(
                            movieId = entry.arguments!!.getInt("id"),
                            onBack = { nav.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
