package com.pasteuri.githubuserbrowser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pasteuri.githubuserbrowser.domain.model.GithubRepo
import com.pasteuri.githubuserbrowser.domain.model.User
import com.pasteuri.githubuserbrowser.domain.model.UserDetailResult
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository
import com.pasteuri.githubuserbrowser.domain.usecase.GetUserDetailUseCase
import com.pasteuri.githubuserbrowser.domain.usecase.SearchUsersUseCase
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
            GithubUserBrowserTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(searchUsersUseCase, getUserDetailUseCase, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Greeting(
    searchUsersUseCase: SearchUsersUseCase,
    getUserDetailUseCase: GetUserDetailUseCase,
    modifier: Modifier = Modifier
) {
    var users: List<User> by remember { mutableStateOf(emptyList()) }
    var userProfile: User? by remember { mutableStateOf(null) }
    var orgProfile: User? by remember { mutableStateOf(null) }
    var userRepoList: List<GithubRepo> by remember { mutableStateOf(emptyList()) }
    var orgRepoList: List<GithubRepo> by remember { mutableStateOf(emptyList()) }

    LaunchedEffect(Unit) {
        searchUsersUseCase(
            query = "lapak",
            searchUserSort = UserRepository.SearchUserSort.JOINED,
            searchOrder = null
        ).collect {
            users = it.getOrNull().orEmpty()
        }
    }
    LaunchedEffect(users) {
        users.filter { it.type == User.Type.USER }.getOrNull(0)?.let { user ->
            getUserDetailUseCase(user.username, user.type).collect { result ->
                when (result) {
                    is UserDetailResult.UserLoaded -> userProfile = result.user
                    is UserDetailResult.RepositoriesLoaded -> userRepoList = result.repos
                    else -> Unit
                }
            }
        }
        users.filter { it.type == User.Type.ORG }.getOrNull(3)?.let { user ->
            getUserDetailUseCase(user.username, user.type).collect { result ->
                when (result) {
                    is UserDetailResult.UserLoaded -> orgProfile = result.user
                    is UserDetailResult.RepositoriesLoaded -> orgRepoList = result.repos
                    else -> Unit
                }
            }
        }
    }

    Column(
        modifier = modifier
    ) {
        Text(
            text = "User",
            modifier = Modifier.padding(top = 20.dp)
        )
        Text(text = userProfile?.username.orEmpty())
        Text(text = "User repos")
        userRepoList.forEach {
            Text(text = it.name)
        }

        Text(
            text = "Org",
            modifier = Modifier.padding(top = 20.dp)
        )
        Text(text = orgProfile?.username.orEmpty())
        Text(text = "Org repos")
        orgRepoList.forEach {
            Text(text = it.name)
        }
    }
}
