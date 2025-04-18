package com.pasteuri.githubuserbrowser.ui.screen.home

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.pasteuri.githubuserbrowser.GithubUserBrowserNavigation
import com.pasteuri.githubuserbrowser.R
import com.pasteuri.githubuserbrowser.UserDetailArg
import com.pasteuri.githubuserbrowser.domain.model.User
import com.pasteuri.githubuserbrowser.domain.model.VisitedUser
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository.SearchOrder
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository.SearchUserSort
import com.pasteuri.githubuserbrowser.ui.component.EmptyLayout
import com.pasteuri.githubuserbrowser.ui.component.InfiniteScrollAppendLoadState
import com.pasteuri.githubuserbrowser.ui.component.OptionSelections
import com.pasteuri.githubuserbrowser.ui.component.UserShimmer
import com.pasteuri.githubuserbrowser.util.reformatEnum
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<HomeViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val userPagingItems: LazyPagingItems<User> = viewModel.usersResultState.collectAsLazyPagingItems()

    val focusRequester = remember { FocusRequester() }
    var showSortDialog by remember { mutableStateOf(false) }
    var showBackToExitToast by remember { mutableStateOf(false) }
    var backPressState by remember { mutableStateOf<BackPress>(BackPress.Idle) }

    if(showBackToExitToast){
        Toast.makeText(context, stringResource(R.string.back_again_to_exit), Toast.LENGTH_SHORT).show()
        showBackToExitToast = false
    }

    LaunchedEffect(backPressState) {
        if (backPressState == BackPress.InitialTouch) {
            delay(2500)
            backPressState = BackPress.Idle
        }
    }

    BackHandler(backPressState == BackPress.Idle) {
        backPressState = BackPress.InitialTouch
        showBackToExitToast = true
    }

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 },
                visible = uiState.searchInput.isNotBlank() && userPagingItems.itemCount > 0
            ) {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    shape = CircleShape,
                    onClick = {
                        showSortDialog = true
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sort),
                        contentDescription = "Sort"
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            SearchTextField(
                text = uiState.searchInput,
                focusRequester = focusRequester,
                onValueChange = { viewModel.searchUsers(it) },
                onClear = { viewModel.clearQuery() },
                onSearch = { viewModel.searchUsers(query = it, withDelay = false) }
            )
            AnimatedVisibility(visible = uiState.cachedVisitedUsers.isNotEmpty() &&
                    userPagingItems.itemCount == 0 && userPagingItems.loadState.refresh != LoadState.Loading) {
                Column {
                    Text(
                        "Recently visited user",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .8f),
                        modifier = Modifier.padding(start = 20.dp, top = 12.dp, end = 16.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        items(uiState.cachedVisitedUsers.size) { index ->
                            val user = uiState.cachedVisitedUsers[index]
                            VisitedUserItem(user) {
                                viewModel.cacheVisitedUser(user)
                                navController.navigate(
                                    GithubUserBrowserNavigation.DetailRoute.createRoute(
                                        UserDetailArg(user.username, user.type)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            if (userPagingItems.loadState.isIdle && uiState.searchInput.isBlank()) {
                EmptyLayout(
                    title = stringResource(R.string.initial_search_title),
                    description = stringResource(R.string.initial_search_desc),
                    action = stringResource(R.string.initial_search_action),
                    actionIcon = Icons.Default.Search
                ) {
                    focusRequester.requestFocus()
                }
            } else if (userPagingItems.loadState.refresh is LoadState.Loading) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    repeat(3) {
                        UserShimmer()
                    }
                }
            } else if (userPagingItems.loadState.refresh is LoadState.Error) {
                EmptyLayout(
                    title = stringResource(R.string.error_search_title),
                    description = stringResource(R.string.error_search_desc),
                    action = stringResource(R.string.error_search_action),
                    actionIcon = Icons.Default.Refresh,
                    actionColor = MaterialTheme.colorScheme.error
                ) {
                    userPagingItems.retry()
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(userPagingItems.itemCount) { index ->
                        val user = userPagingItems[index]
                        UserItem(user, onClick = {
                            user?.let {
                                viewModel.cacheVisitedUser(VisitedUser(it.username, it.type, it.avatarUrl))
                                navController.navigate(
                                    GithubUserBrowserNavigation.DetailRoute.createRoute(
                                        UserDetailArg(it.username, it.type)
                                    )
                                )
                            }
                        })
                    }
                    if (userPagingItems.itemCount > 0) {
                        item {
                            InfiniteScrollAppendLoadState(userPagingItems.loadState.append) {
                                userPagingItems.retry()
                            }
                        }
                    }
                }
            }
        }

        if (showSortDialog) {
            ListOptionsDialog (
                initialSort = uiState.searchSort,
                initialOrder = uiState.searchOrder,
                onApplied = { listSort, listOrder ->
                    viewModel.applyListOption(listSort, listOrder)
                },
                onDismissRequest = {
                    showSortDialog = false
                }
            )
        }
    }
}

@Composable
private fun UserItem(
    user: User?,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.surfaceContainer, CircleShape)
        ) {
            AsyncImage(
                model = user?.avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = user?.username.orEmpty(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .8f)
        )
    }
}

@Composable
private fun VisitedUserItem(
    user: VisitedUser,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = .3f),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = user.username,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .8f)
        )
    }
}

@Composable
private fun SearchTextField(
    text: String,
    focusRequester: FocusRequester,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
    onSearch: (String) -> Unit
) {
    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
        placeholder = { Text(stringResource(R.string.search_placeholder)) },
        singleLine = true,
        modifier = Modifier
            .focusRequester(focusRequester)
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(100.dp),
        colors = TextFieldDefaults.colors(),
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search icon",
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(onSearch = {
            onSearch(text)
        }),
        trailingIcon = {
            if (text.isNotBlank()) {
                Icon(
                    Icons.Default.Clear,
                    tint = MaterialTheme.colorScheme.surface,
                    contentDescription = "Clear search query",
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .3f),
                            shape = CircleShape
                        )
                        .clickable(onClick = onClear)
                        .padding(2.dp)
                )
            }
        }
    )
}

@Composable
private fun ListOptionsDialog(
    initialSort: SearchUserSort,
    initialOrder: SearchOrder,
    onApplied: (SearchUserSort, SearchOrder) -> Unit,
    onDismissRequest: () -> Unit
) {
    val sortOptions = SearchUserSort.entries.map {
        it to it.name.reformatEnum()
    }
    val orderOptions = SearchOrder.entries.map {
        it to it.name.reformatEnum()
    }
    var selectedSort by remember { mutableStateOf(initialSort) }
    var selectedOrder by remember { mutableStateOf(initialOrder) }

    Dialog(onDismissRequest) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            Column {
                LazyColumn(modifier = Modifier.padding(20.dp)) {
                    item {
                        OptionSelections(stringResource(R.string.option_sort), sortOptions, selectedSort) {
                            selectedSort = it
                        }
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        OptionSelections(stringResource(R.string.option_order), orderOptions, selectedOrder) {
                            selectedOrder = it
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    TextButton(
                        onClick = {
                            onApplied(selectedSort, selectedOrder)
                            onDismissRequest()
                        },
                    ) {
                        Text(
                            stringResource(R.string.option_apply),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }
    }
}

sealed class BackPress {
    data object Idle : BackPress()
    data object InitialTouch : BackPress()
}
