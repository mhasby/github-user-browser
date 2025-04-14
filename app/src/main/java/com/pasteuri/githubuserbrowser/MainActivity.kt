package com.pasteuri.githubuserbrowser

import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pasteuri.githubuserbrowser.data.remote.RetrofitBuilder
import com.pasteuri.githubuserbrowser.data.remote.repository.DefaultGithubRepoRepository
import com.pasteuri.githubuserbrowser.data.remote.repository.DefaultUserRepository
import com.pasteuri.githubuserbrowser.data.remote.service.GithubRepoService
import com.pasteuri.githubuserbrowser.data.remote.service.SearchService
import com.pasteuri.githubuserbrowser.data.remote.service.UserService
import com.pasteuri.githubuserbrowser.domain.model.GithubRepo
import com.pasteuri.githubuserbrowser.domain.model.User
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository
import com.pasteuri.githubuserbrowser.ui.theme.GithubUserBrowserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GithubUserBrowserTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    var userProfile: User? by remember { mutableStateOf(null) }
    var orgProfile: User? by remember { mutableStateOf(null) }
    var userRepoList: List<GithubRepo> by remember { mutableStateOf(emptyList()) }
    var orgRepoList: List<GithubRepo> by remember { mutableStateOf(emptyList()) }

    LaunchedEffect(Unit) {
        val userRepository = DefaultUserRepository(
            {RetrofitBuilder.getInstance().create(UserService::class.java)},
            {RetrofitBuilder.getInstance().create(SearchService::class.java)}
        )
        val githubRepoRepository = DefaultGithubRepoRepository(
            {RetrofitBuilder.getInstance().create(GithubRepoService::class.java)}
        )
        val usersResult = userRepository.searchUsers("lapak", 50, 1, sort = UserRepository.SearchUserSort.JOINED, order = null)
        usersResult.getOrNull()?.let { users ->
            Log.d("TESTT", "User")
            users.filter { it.type == User.Type.USER }.getOrNull(0)?.let { user ->
                userProfile = userRepository.getUserDetail(user.id).getOrNull()
                userRepoList = githubRepoRepository.getRepoByUser(user, 2, 1, null, null, null).getOrNull().orEmpty()
            }
            Log.d("TESTT", "Org")
            users.filter { it.type == User.Type.ORG }.getOrNull(3)?.let { user ->
                orgProfile = userRepository.getUserDetail(user.id).getOrNull()
                orgRepoList = githubRepoRepository.getRepoByUser(user, 2, 1, null, null, null).getOrNull().orEmpty()
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
        Text(text = userProfile?.username.orEmpty())
        Text(text = "Org repos")
        orgRepoList.forEach {
            Text(text = it.name)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GithubUserBrowserTheme {
        Greeting("Android")
    }
}