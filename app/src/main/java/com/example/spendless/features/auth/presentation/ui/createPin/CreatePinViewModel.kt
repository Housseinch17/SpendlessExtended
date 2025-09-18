package com.example.spendless.features.auth.presentation.ui.createPin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendless.features.auth.presentation.designsystem.Constants.DELETE_CHAR
import com.example.spendless.features.auth.presentation.ui.common.PinActions
import com.example.spendless.features.auth.presentation.ui.common.PinEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePinViewModel @Inject constructor(
    private val saveHandleStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(CreatePinUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<PinEvents>()
    val events = _events.receiveAsFlow()

    init {
        saveUsername()
    }

    fun onActions(pinActions: PinActions) {
        when (pinActions) {
            is PinActions.UpdatePin -> updatePin(newPin = pinActions.newPin)
            PinActions.NavigateBack -> navigateBack()
            else -> {}
        }
    }

    private fun saveUsername() {
        val username = saveHandleStateHandle.get<String>("username") ?: ""
        _state.update { newState ->
            newState.copy(
                username = username
            )
        }
    }

    private fun updatePin(newPin: String) {
        val currentPin = _state.value.pin
        val pin = when{
            newPin == DELETE_CHAR -> currentPin.dropLast(1)
            currentPin.length < 5 -> currentPin + newPin
            else -> currentPin
        }

        _state.update { newState ->
            newState.copy(
                pin = pin
            )
        }
        if(pin.length == 5){
            val username = _state.value.username
            viewModelScope.launch {
                _state.update { newState->
                    newState.copy(
                        pin = ""
                    )
                }
                _events.send(PinEvents.NavigateToRepeatPin(username = username, pin = pin))
            }
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _events.send(PinEvents.NavigateBack)
        }
    }
}