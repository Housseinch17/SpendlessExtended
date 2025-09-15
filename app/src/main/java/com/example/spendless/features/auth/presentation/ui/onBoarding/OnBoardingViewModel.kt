package com.example.spendless.features.auth.presentation.ui.onBoarding

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendless.core.database.user.model.Currency
import com.example.spendless.core.domain.model.User
import com.example.spendless.core.domain.util.Result
import com.example.spendless.features.auth.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

sealed interface OnBoardingEvents {
    data object NavigateBack : OnBoardingEvents
    data class Dashboard(val username: String) : OnBoardingEvents
}

sealed interface OnBoardingActions {
    data object NavigateBack : OnBoardingActions
    data object StartTracking : OnBoardingActions
    data object OnExpand : OnBoardingActions
    data class UpdateExpensesFormat(val expenses: String) : OnBoardingActions
    data class UpdateDecimalSeparator(val decimal: String) : OnBoardingActions
    data class UpdateThousandsSeparator(val thousands: String) : OnBoardingActions
    data class UpdateSelectedCurrency(val currency: Currency) : OnBoardingActions
    data object CloseExpand : OnBoardingActions

}

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val saveHandleStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(OnBoardingUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<OnBoardingEvents>()
    val events = _events.receiveAsFlow()

    init {
        saveUsernameAndPin()
    }

    private fun saveUsernameAndPin() {
        val username = saveHandleStateHandle.get<String>("username") ?: ""
        val pin = saveHandleStateHandle.get<String>("pin") ?: ""
        _state.update { newState ->
            newState.copy(
                username = username,
                pin = pin
            )
        }
    }

    fun onActions(onBoardingActions: OnBoardingActions) {
        when (onBoardingActions) {
            OnBoardingActions.NavigateBack -> navigateBack()
            OnBoardingActions.StartTracking -> startTracking()
            OnBoardingActions.CloseExpand -> closeExpand()
            is OnBoardingActions.UpdateExpensesFormat -> updateExpensesFormat(expenses = onBoardingActions.expenses)
            is OnBoardingActions.UpdateDecimalSeparator -> updateDecimalSeparator(decimal = onBoardingActions.decimal)
            is OnBoardingActions.UpdateThousandsSeparator -> updateThousandsSeparator(thousands = onBoardingActions.thousands)
            is OnBoardingActions.UpdateSelectedCurrency -> updateCurrency(currency = onBoardingActions.currency)
            is OnBoardingActions.OnExpand -> onExpand()
        }
    }

    private fun updateExpensesFormat(expenses: String) {
        _state.update { newState ->
            newState.copy(
                preferencesFormat = newState.preferencesFormat.copy(expenses = expenses)
            )
        }
    }

    private fun updateDecimalSeparator(decimal: String) {
        _state.update { newState ->
            newState.copy(
                preferencesFormat = newState.preferencesFormat.copy(decimalSeparator = decimal)
            )
        }
    }

    private fun updateThousandsSeparator(thousands: String) {
        _state.update { newState ->
            newState.copy(
                preferencesFormat = newState.preferencesFormat.copy(thousandsSeparator = thousands)
            )
        }
    }

    private fun updateCurrency(currency: Currency) {
        _state.update { newState ->
            newState.copy(
                preferencesFormat = newState.preferencesFormat.copy(currency = currency)
            )
        }
    }

    private fun onExpand() {
        val isExpanded = _state.value.isExpanded
        _state.update { newState ->
            newState.copy(
                isExpanded = !isExpanded
            )
        }
    }

    private fun closeExpand() {
        _state.update { newState ->
            newState.copy(
                isExpanded = false
            )
        }
    }


    private fun startTracking() {
        _state.update { newState->
            newState.copy(isButtonLoading = true)
        }
        val state = _state.value
        val username = state.username
        val pin = state.pin
        val total = state.total
        val preferencesFormat = state.preferencesFormat
        viewModelScope.launch {
            val user = User(
                username = username,
                pin = pin,
                total = total,
                preferences = preferencesFormat
            )

            //show circular progress indicator for 1 seconds
            delay(1.seconds)
            val result = userRepository.insertUser(user = user)
            when (result) {
                is Result.Error -> {
                    Timber.tag("MyTag").e("startTracking: error: ${result.error}")
                    _state.update { newState->
                        newState.copy(isButtonLoading = false)
                    }
                }
                is Result.Success -> {
                    Timber.tag("MyTag").d("startTracking(): success")
                    _state.update { newState->
                        newState.copy(isButtonLoading = false)
                    }
                    _events.send(OnBoardingEvents.Dashboard(username = username))
                }
            }
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _events.send(OnBoardingEvents.NavigateBack)
        }
    }

}