package com.example.spendless.features.finance.domain

import kotlinx.coroutines.flow.Flow

interface LifecycleObserver {
    val isInForeground: Flow<Boolean>
}