package com.example.spendless.features.auth.presentation.ui.screens.onBoarding

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendless.R
import com.example.spendless.core.data.database.user.model.Currency
import com.example.spendless.core.data.database.user.model.Security
import com.example.spendless.core.domain.auth.AuthInfo
import com.example.spendless.core.domain.auth.SessionStorage
import com.example.spendless.core.domain.model.User
import com.example.spendless.core.domain.time.TimeRepository
import com.example.spendless.core.domain.util.DataError
import com.example.spendless.core.domain.util.Result
import com.example.spendless.core.presentation.ui.UiText
import com.example.spendless.features.auth.domain.UserRepository
import com.example.spendless.features.finance.domain.TransactionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
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
    data class ShowToast(val showText: UiText): OnBoardingEvents
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
    private val sessionStorage: SessionStorage,
    private val transactionsRepository: TransactionsRepository,
    private val timeRepository: TimeRepository,

) : ViewModel() {
    private val _state = MutableStateFlow(OnBoardingUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<OnBoardingEvents>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            launch {
                saveUsernameAndPin()
            }
            launch {
                setInitialPreferencesFormat()
            }
            getUserTotal()
        }

    }

    private suspend fun saveUsernameAndPin() {
        //if i navigated from settings screen to onBoardingScreen send username and pin as null
        //if username is sent as null we have to get current username from sessionStorage
        val username = saveHandleStateHandle.get<String>("username")
        val newUsername = username ?: sessionStorage.getAuthInfo()!!.username
        val pin = saveHandleStateHandle.get<String>("pin") ?: ""
        _state.update { newState ->
            newState.copy(
                username = newUsername,
                pin = pin
            )
        }
    }

    private suspend fun setInitialPreferencesFormat() {
        val username = saveHandleStateHandle.get<String>("username")
        val newUsername = username ?: sessionStorage.getAuthInfo()!!.username
        if (username.isNullOrEmpty()) {
            val result = userRepository.getPreferencesByUsername(
                username = newUsername
            )
            if (result is Result.Success) {
                _state.update { newState ->
                    newState.copy(
                        preferencesFormat = result.data
                    )
                }
            }
        }
    }

    private fun getUserTotal() {
        viewModelScope.launch {
            val authInfo = sessionStorage.getAuthInfo()
            when (authInfo) {
                //if not logged in
                null -> {
                    Timber.tag("MyTag").d("here: null")
                    _state.update { newState ->
                        newState.copy(
                            total = "1038245"
                        )
                    }
                }

                else -> {
                    val total = transactionsRepository.getNetTotalForUser()
                    total.collect { total ->
                        Timber.tag("MyTag").d("total: $total")
                        _state.update { newState ->
                            newState.copy(
                                total = total
                            )
                        }

                    }
                }
            }
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
        val userSent = saveHandleStateHandle.get<String>("username")
        Timber.tag("MyTag").d("userSent: $userSent")
        if (userSent.isNullOrEmpty()) {
            updateUserPreferencesFormat()
        } else {
            _state.update { newState ->
                newState.copy(isButtonLoading = true)
            }
            val state = _state.value
            val username = state.username
            val pin = state.pin
            val preferencesFormat = state.preferencesFormat
            //default value
            val security = Security()
            viewModelScope.launch {
                val user = User(
                    username = username,
                    pin = pin,
                    preferences = preferencesFormat,
                    security = security
                )

                //show circular progress indicator for 1 seconds
                delay(1.seconds)
                val result = userRepository.insertUser(user = user)
                when (result) {
                    is Result.Error -> {
                        if (result.error is DataError.Local.Unknown) {
                            Timber.tag("MyTag")
                                .e("startTracking: error: ${result.error.unknownError}")
                        } else {
                            Timber.tag("MyTag").e("startTracking: error: ${result.error}")
                        }
                        _state.update { newState ->
                            newState.copy(isButtonLoading = false)
                        }
                    }

                    is Result.Success -> {
                        Timber.tag("MyTag").d("startTracking(): success")
                        val state = _state.value
                        val currentTime = timeRepository.getCurrentTime()

                        async {
                            //default value for username
                            sessionStorage.setAuthInfo(
                                authInfo = AuthInfo(
                                    username = state.username,
                                    currentTimeLoggedIn = currentTime
//                                    security = security,
//                                    preferencesFormat = preferencesFormat,
                                )
                            )
                            _state.update { newState ->
                                newState.copy(isButtonLoading = false)
                            }
                        }.await()

                        _events.send(OnBoardingEvents.Dashboard(username = username))
                    }
                }
            }
        }
    }

    private fun updateUserPreferencesFormat() {
        _state.update { newState ->
            newState.copy(isButtonLoading = true)
        }
        val username = _state.value.username
        val preferencesFormat = _state.value.preferencesFormat
        viewModelScope.launch {
            val result = userRepository.updatePreferencesFormat(
                username = username,
                preferencesFormat = preferencesFormat
            )
            when (result) {
                is Result.Error -> {
                    Timber.tag("MyTag").e("updateUserPreferencesFormat(): ${result.error}")
                }

                is Result.Success -> {
                    showToast()
                    //i dont have to set isButtonLoading to false, cause navigateBack will clear this from backstack
                    navigateBack()
                }
            }
        }
    }

    private fun showToast(){
        viewModelScope.launch {
            _events.send(OnBoardingEvents.ShowToast(UiText.StringResource(R.string.successfully_saved)))
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _events.send(OnBoardingEvents.NavigateBack)
        }
    }

}