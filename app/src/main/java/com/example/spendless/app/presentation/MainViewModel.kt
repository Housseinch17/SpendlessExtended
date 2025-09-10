package com.example.spendless.app.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel(){
    private val _state = MutableStateFlow(MainUiState())
    val state = _state.asStateFlow()

    init {

    }

}