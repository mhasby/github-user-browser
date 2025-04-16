package com.pasteuri.githubuserbrowser.ui.screen.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.pasteuri.githubuserbrowser.GithubUserBrowserNavigation
import com.pasteuri.githubuserbrowser.domain.model.GithubRepo
import com.pasteuri.githubuserbrowser.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavController) {
    val viewModel = hiltViewModel<DetailViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val reposPagingItems: LazyPagingItems<GithubRepo> = viewModel.reposResultState.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            UserDetailAppBar(uiState.user) {
                navController.popBackStack()
            }
        }
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding).fillMaxSize()) {
            PullToRefreshBox(
                isRefreshing = reposPagingItems.loadState.refresh is LoadState.Loading,
                onRefresh = { reposPagingItems.refresh() },
                modifier = Modifier.fillMaxSize()
            ) {
                if (reposPagingItems.loadState.refresh is LoadState.Error) {
                    Text(
                        text = "Failed to load data. Tap to retry.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                reposPagingItems.retry()
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(reposPagingItems.itemCount) { index ->
                            Column(
                                modifier = Modifier
                                    .clickable {
                                        navController.navigate(
                                            GithubUserBrowserNavigation.GithubRepoRoute.createRoute(
                                                title = reposPagingItems[index]?.name.orEmpty(),
                                                url = reposPagingItems[index]?.url.orEmpty(),
                                            )
                                        )
                                    }
                                    .padding(vertical = 8.dp, horizontal = 16.dp)
                            ) {
                                Text(reposPagingItems[index]?.name.orEmpty())
                                Text(reposPagingItems[index]?.description.orEmpty())
                                Text(reposPagingItems[index]?.language.orEmpty())
                                Text("Stars ${reposPagingItems[index]?.stars ?: 0}")
                            }
                        }
                        when (reposPagingItems.loadState.append) {
                            is LoadState.Loading -> item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .align(Alignment.Center)
                                    )
                                }
                            }
                            is LoadState.Error -> item {
                                Text(
                                    text = "Failed to load more items. Tap to try again.",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            reposPagingItems.retry()
                                        }
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                )
                            }
                            is LoadState.NotLoading -> Unit
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailAppBar(
    user: User?,
    onBackClick: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = user?.avatarUrl,
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(text = user?.username.orEmpty(), style = MaterialTheme.typography.titleMedium)
                    Text(text = user?.name.orEmpty(), style = MaterialTheme.typography.bodySmall)
                    Row {
                        Text(text = "Follower : ${user?.followers ?: 0}", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = "Following : ${user?.following ?: 0}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}