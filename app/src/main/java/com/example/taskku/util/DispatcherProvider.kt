package com.example.taskku.util

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object DispatcherProvider {
    @VisibleForTesting
    var overrideComputation: CoroutineDispatcher? = null

    /**
     * Returns Dispatchers.Default for background CPU computations in production,
     * or Dispatchers.Main if running in a test environment with a TestDispatcher installed.
     */
    val defaultComputation: CoroutineDispatcher
        get() = overrideComputation ?: try {
            val main = Dispatchers.Main
            if (main.javaClass.name.contains("Test")) {
                main
            } else {
                Dispatchers.Default
            }
        } catch (_: Throwable) {
            Dispatchers.Default
        }
}
