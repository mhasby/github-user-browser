package com.pasteuri.githubuserbrowser.ui.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.pasteuri.githubuserbrowser.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val viewModel = hiltViewModel<HomeViewModel>()
    val userPagingItems: LazyPagingItems<User> = viewModel.usersResultState.collectAsLazyPagingItems()

    PullToRefreshBox(
        isRefreshing = userPagingItems.loadState.refresh is LoadState.Loading,
        onRefresh = { userPagingItems.refresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 16.dp)
        ) {
            items(userPagingItems.itemCount) { index ->
                Text(
                    text = userPagingItems[index]?.username.orEmpty(),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            userPagingItems.apply {
                when {
                    loadState.refresh is LoadState.Error -> {
                        item {
                            Text(
                                text = "Failed to load data. Tap to retry.",
                                modifier = Modifier.clickable {
                                    userPagingItems.retry()
                                },
                            )
                        }
                    }

                    loadState.append is LoadState.Loading -> {
                        item { CircularProgressIndicator(modifier = Modifier.size(24.dp)) }
                    }

                    loadState.append is LoadState.Error -> {
                        item {
                            Text(
                                text = "Failed to load more items. Tap to try again.",
                                modifier = Modifier.clickable {
                                    userPagingItems.retry()
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}