package com.pasteuri.githubuserbrowser.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.pasteuri.githubuserbrowser.domain.model.User
import com.pasteuri.githubuserbrowser.domain.usecase.SearchUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val searchUsersUseCase: SearchUsersUseCase
) : ViewModel() {

    private val _usersResultState: MutableStateFlow<PagingData<User>> = MutableStateFlow(value = PagingData.empty())
    val usersResultState: MutableStateFlow<PagingData<User>> get() = _usersResultState

    init {
        searchUsers("mhasby")
    }

    fun searchUsers(query: String) = viewModelScope.launch(Dispatchers.IO) {
        searchUsersUseCase(query)
            .distinctUntilChanged()
            .cachedIn(viewModelScope)
            .collect {
                _usersResultState.value = it
            }
    }
}