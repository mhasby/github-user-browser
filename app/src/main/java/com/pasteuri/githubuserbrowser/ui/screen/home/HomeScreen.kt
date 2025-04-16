package com.pasteuri.githubuserbrowser.ui.screen.home

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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.pasteuri.githubuserbrowser.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val viewModel = hiltViewModel<HomeViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val userPagingItems: LazyPagingItems<User> = viewModel.usersResultState.collectAsLazyPagingItems()

    Column {
        TextField(
            value = uiState.searchInput,
            onValueChange = { viewModel.searchUsers(it) },
            placeholder = { Text("Type to search user") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                if (uiState.searchInput.isNotBlank()) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            viewModel.clearQuery()
                        }
                    )
                }
            }
        )
        PullToRefreshBox(
            isRefreshing = userPagingItems.loadState.refresh is LoadState.Loading,
            onRefresh = { userPagingItems.refresh() },
            modifier = Modifier.fillMaxSize()
        ) {
            if (userPagingItems.loadState.refresh is LoadState.Error) {
                Text(
                    text = "Failed to load data. Tap to retry.",
                    modifier = Modifier.fillMaxWidth().clickable {
                        userPagingItems.retry()
                    }.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(userPagingItems.itemCount) { index ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            AsyncImage(
                                model = userPagingItems[index]?.avatarUrl,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp).clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = userPagingItems[index]?.username.orEmpty(),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    when (userPagingItems.loadState.append) {
                        is LoadState.Loading -> item {
                            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp).align(Alignment.Center))
                            }
                        }
                        is LoadState.Error -> item {
                            Text(
                                text = "Failed to load more items. Tap to try again.",
                                modifier = Modifier.fillMaxWidth().clickable {
                                    userPagingItems.retry()
                                }.padding(horizontal = 16.dp, vertical = 8.dp),
                            )
                        }
                        is LoadState.NotLoading -> Unit
                    }
                }
            }
        }
    }
}