package com.pasteuri.githubuserbrowser.ui.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.pasteuri.githubuserbrowser.UserDetailArg
import com.pasteuri.githubuserbrowser.domain.model.GithubRepo
import com.pasteuri.githubuserbrowser.domain.model.User
import com.pasteuri.githubuserbrowser.domain.repository.GithubRepoRepository.ListFilterType
import com.pasteuri.githubuserbrowser.domain.repository.GithubRepoRepository.ListOrder
import com.pasteuri.githubuserbrowser.domain.repository.GithubRepoRepository.ListSort
import com.pasteuri.githubuserbrowser.domain.usecase.GetGithubRepoByUserUseCase
import com.pasteuri.githubuserbrowser.domain.usecase.GetUserDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getUserDetailUseCase: GetUserDetailUseCase,
    private val getGithubRepoByUserUseCase: GetGithubRepoByUserUseCase
) : ViewModel() {

    private val userArg: UserDetailArg? by lazy {
        val userJson: String? = savedStateHandle["user"]
        Gson().fromJson(userJson, UserDetailArg::class.java)
    }

    private val _uiState: MutableStateFlow<DetailUiState> = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> get() = _uiState

    private val _reposResultState = MutableStateFlow<PagingData<GithubRepo>>(PagingData.empty())
    val reposResultState: StateFlow<PagingData<GithubRepo>> = _reposResultState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch { getUserDetail() }
            launch { getGithubRepo() }
        }
    }

    fun applyListOption(filterType: ListFilterType, sort: ListSort, order: ListOrder) {
        _uiState.update {
            it.copy(
                repoListFilter = filterType,
                repoListSort = sort,
                repoListOrder = order
            )
        }
        viewModelScope.launch { getGithubRepo() }
    }

    private suspend fun getUserDetail() {
        _uiState.update { it.copy(isLoadingUser = true) }
        val result = getUserDetailUseCase(userArg?.username.orEmpty())
        _uiState.update { it.copy(isLoadingUser = false) }
        result.onSuccess { value ->
            _uiState.update {
                it.copy(
                    isLoadingUser = false,
                    user = value,
                    errorUserDetail = null
                )
            }
        }.onFailure { error ->
            _uiState.update {
                it.copy(
                    isLoadingUser = false,
                    errorUserDetail = error.cause
                )
            }
        }
    }

    private suspend fun getGithubRepo() {
        getGithubRepoByUserUseCase(
            username = userArg?.username.orEmpty(),
            type = userArg?.type ?: User.Type.USER,
            filterType = _uiState.value.repoListFilter,
            sort = _uiState.value.repoListSort,
            order = _uiState.value.repoListOrder
        )
            .distinctUntilChanged()
            .cachedIn(viewModelScope)
            .collect {
                _reposResultState.value = it
            }
    }
}

data class DetailUiState(
    val isLoadingUser: Boolean = false,
    val errorUserDetail: Throwable? = null,
    val user: User? = null,
    val repoListFilter: ListFilterType = ListFilterType.ALL,
    val repoListSort: ListSort = ListSort.PUSHED,
    val repoListOrder: ListOrder = ListOrder.DESC,
)
