package com.example.spendless.core.domain.util

sealed interface Result<out D, out E: Error> {
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Error<out E: com.example.spendless.core.domain.util.Error>(val error: E) : Result<Nothing, E>
}

inline fun<T,E: Error,R, LE: Error> Result<T,E>.mapToResult(
    success: (T) -> R,
    localError: (E) -> LE
): Result<R,LE> {
    return when (this){
        is Result.Error -> Result.Error(localError(error))
        is Result.Success -> Result.Success(success(data))
    }
}

fun <T, E : Error> Result<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return when (this) {
        is Result.Success -> Result.Success(Unit)
        is Result.Error -> Result.Error(error)
    }
}

typealias EmptyResult<E> = Result<Unit,E>