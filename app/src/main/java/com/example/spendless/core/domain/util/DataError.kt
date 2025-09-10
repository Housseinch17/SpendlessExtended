package com.example.spendless.core.domain.util

sealed interface DataError: Error {
    sealed interface Local : DataError {
        data object DiskFull: Local
        data class Unknown(val unknownError: String): Local
    }
}