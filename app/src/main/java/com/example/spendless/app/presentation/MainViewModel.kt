package com.example.spendless.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendless.core.domain.auth.SessionStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sessionStorage: SessionStorage,
): ViewModel(){
    private val _state = MutableStateFlow(MainUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getAuthInfo()
        }
    }

    private suspend fun getAuthInfo() {
        isCheckingAuth(isCheckingAuth = true)
        val authInfo = sessionStorage.getAuthInfo()
        Timber.tag("MyTag").d("authInfo: $authInfo")
        _state.update { newState ->
            newState.copy(
                isLoggedInPreviously = authInfo != null
            )
        }
        isCheckingAuth(isCheckingAuth = false)
    }

    private fun isCheckingAuth(isCheckingAuth: Boolean) {
        _state.update { newState ->
            newState.copy(
                isCheckingAuth = isCheckingAuth
            )
        }
    }
}