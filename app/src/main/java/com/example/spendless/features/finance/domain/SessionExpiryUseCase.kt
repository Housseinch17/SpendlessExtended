package com.example.spendless.features.finance.domain

interface SessionExpiryUseCase {
    suspend operator fun invoke(): Boolean
}