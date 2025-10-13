package com.example.spendless.features.finance.presentation.ui.screens.security

import com.example.spendless.core.data.constant.Constants
import com.example.spendless.core.data.database.user.model.Security
import com.example.spendless.features.auth.data.model.CounterPerTimeUnit
import com.example.spendless.R

data class SecurityUiState(
    val username: String = "",
    val isLoading: Boolean = true,
    val isButtonsEnabled: Boolean = true,
    val isButtonLoading: Boolean = false,
    val security: Security = Security(),
    val expiryDurationList: List<CounterPerTimeUnit> = Constants.expiryDuration,
    val lockedDurationList: List<CounterPerTimeUnit> = Constants.lockedDuration,
    val biometricList: List<Int> = Constants.biometricList
){
    val selectedLockedDuration: CounterPerTimeUnit = security.lockedOutDuration
    val selectedExpiryDuration: CounterPerTimeUnit = security.sessionExpiry
    val selectedBiometric: Int = if(security.withBiometric) R.string.enable else R.string.disable
}
