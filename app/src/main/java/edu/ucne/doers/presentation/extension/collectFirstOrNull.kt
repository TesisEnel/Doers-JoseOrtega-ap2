package edu.ucne.doers.presentation.extension

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

suspend fun <T> Flow<T>.collectFirstOrNull(): T? {
    return this.firstOrNull()
}