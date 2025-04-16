package com.pasteuri.githubuserbrowser.ui.screen.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.pasteuri.githubuserbrowser.GithubUserBrowserNavigation
import com.pasteuri.githubuserbrowser.R
import com.pasteuri.githubuserbrowser.domain.model.GithubRepo
import com.pasteuri.githubuserbrowser.domain.model.User
import com.pasteuri.githubuserbrowser.ui.component.EmptyLayout
import com.pasteuri.githubuserbrowser.ui.component.InfiniteScrollAppendLoadState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavController) {
    val viewModel = hiltViewModel<DetailViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val reposPagingItems: LazyPagingItems<GithubRepo> = viewModel.reposResultState.collectAsLazyPagingItems()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            UserDetailAppBar(uiState.user, scrollBehavior) {
                navController.popBackStack()
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            PullToRefreshBox(
                isRefreshing = reposPagingItems.loadState.refresh is LoadState.Loading,
                onRefresh = { reposPagingItems.refresh() },
                modifier = Modifier.fillMaxSize()
            ) {
                if (reposPagingItems.loadState.refresh is LoadState.Error) {
                    EmptyLayout(
                        title = stringResource(R.string.error_detail_title),
                        description = stringResource(R.string.error_detail_desc),
                        action = stringResource(R.string.error_detail_action),
                        actionIcon = Icons.Default.Refresh,
                        actionColor = MaterialTheme.colorScheme.error
                    ) {
                        reposPagingItems.retry()
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(reposPagingItems.itemCount) { index ->
                            RepositoryItem(reposPagingItems[index]) {
                                navController.navigate(
                                    GithubUserBrowserNavigation.GithubRepoRoute.createRoute(
                                        title = reposPagingItems[index]?.name.orEmpty(),
                                        url = reposPagingItems[index]?.url.orEmpty(),
                                    )
                                )
                            }
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.surfaceContainer
                            )
                        }
                        item {
                            InfiniteScrollAppendLoadState(reposPagingItems.loadState.append) {
                                reposPagingItems.retry()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RepositoryItem(
    repository: GithubRepo?,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 24.dp)
    ) {
        Text(
            repository?.name.orEmpty(),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (!repository?.description.isNullOrBlank()) {
            Text(
                repository?.description.orEmpty(),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .8f)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                repository?.language ?: "-",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                Icons.Default.Star,
                tint = Color(0xFFF3A257),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "${repository?.stars ?: 0}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailAppBar(
    user: User?,
    scrollBehavior: TopAppBarScrollBehavior,
    onBackClick: () -> Unit
) {
    val isCollapsed = remember { derivedStateOf { scrollBehavior.state.collapsedFraction > 0.5 } }

    LargeTopAppBar(
        scrollBehavior = scrollBehavior,
        expandedHeight = 190.dp,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        title = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = user?.avatarUrl,
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(if(isCollapsed.value) 40.dp else 48.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = user?.username.orEmpty(),
                            style = MaterialTheme.typography.titleLarge
                        )
                        if (!isCollapsed.value) {
                            Text(
                                text = user?.name.orEmpty(),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row {
                                Text(
                                    text = stringResource(R.string.profile_follower, user?.followers ?: 0),
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = stringResource(R.string.profile_following, user?.following ?: 0),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (!isCollapsed.value && !user?.bio.isNullOrBlank()) {
                    Text(
                        text = user?.bio.orEmpty(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            scrolledContainerColor = MaterialTheme.colorScheme.primary,
        )
    )
}