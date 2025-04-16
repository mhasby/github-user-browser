package com.pasteuri.githubuserbrowser.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.pasteuri.githubuserbrowser.domain.model.User
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository.SearchOrder
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository.SearchUserSort
import com.pasteuri.githubuserbrowser.domain.usecase.SearchUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val searchUsersUseCase: SearchUsersUseCase
) : ViewModel() {

    private val _usersResultState: MutableStateFlow<PagingData<User>> = MutableStateFlow(value = PagingData.empty())
    val usersResultState: StateFlow<PagingData<User>> get() = _usersResultState

    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> get() = _uiState

    private var debounceJob: Job? = null

    fun searchUsers(query: String) {
        debounceJob?.cancel()
        if (query.isBlank()) {
            clearQuery()
            return
        }
        _uiState.update { state ->
            state.copy(searchInput = query)
        }
        debounceJob = viewModelScope.launch(Dispatchers.IO) {
            delay(SEARCH_INPUT_DELAY)
            searchUsersUseCase(query, uiState.value.searchSort, uiState.value.searchOrder)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect {
                    _usersResultState.value = it
                }
        }
    }

    fun clearQuery() {
        _usersResultState.value = PagingData.empty()
        _uiState.update { state ->
            state.copy(searchInput = "")
        }
    }

    fun applyListOption(sort: SearchUserSort, order: SearchOrder) {
        _uiState.update {
            it.copy(
                searchSort = sort,
                searchOrder = order
            )
        }
        viewModelScope.launch {
            searchUsers(uiState.value.searchInput)
        }
    }

    companion object {
        private const val SEARCH_INPUT_DELAY = 500L
    }
}

data class HomeUiState(
    val searchInput: String = "",
    val searchSort: SearchUserSort = SearchUserSort.FOLLOWERS,
    val searchOrder: SearchOrder = SearchOrder.DESC,
)
