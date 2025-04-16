package com.pasteuri.githubuserbrowser

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pasteuri.githubuserbrowser.domain.usecase.GetUserDetailUseCase
import com.pasteuri.githubuserbrowser.domain.usecase.SearchUsersUseCase
import com.pasteuri.githubuserbrowser.ui.screen.detail.DetailScreen
import com.pasteuri.githubuserbrowser.ui.screen.detail.RepoDetailWebView
import com.pasteuri.githubuserbrowser.ui.screen.home.HomeScreen
import com.pasteuri.githubuserbrowser.ui.theme.GithubUserBrowserTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var searchUsersUseCase: SearchUsersUseCase

    @Inject
    lateinit var getUserDetailUseCase: GetUserDetailUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GithubUserBrowserApp()
        }
    }
}

@Composable
fun GithubUserBrowserApp() {
    GithubUserBrowserTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = GithubUserBrowserNavigation.HomeRoute.routeName,
        ) {
            composable(GithubUserBrowserNavigation.HomeRoute.routeName) {
                HomeScreen(navController)
            }
            composable(
                GithubUserBrowserNavigation.DetailRoute.routeName) {
                DetailScreen(navController)
            }
            composable(GithubUserBrowserNavigation.GithubRepoRoute.routeName) { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title").orEmpty()
                val url = Uri.decode(backStackEntry.arguments?.getString("url").orEmpty())
                RepoDetailWebView(title, url, navController)
            }
        }
    }
}
