package com.example.spendless.core.domain.util

sealed interface DataError: Error {
    sealed interface Local : DataError {
        data class Unknown(val unknownError: String): Local
    }
}