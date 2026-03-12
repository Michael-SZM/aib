package com.icon.aibrowserasistor.common

sealed class OperationResult<out T> {
    data class Success<T>(val data: T) : OperationResult<T>()
    data class Error(val throwable: Throwable) : OperationResult<Nothing>()
}
