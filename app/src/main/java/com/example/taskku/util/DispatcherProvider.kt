package com.example.taskku.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object DispatcherProvider {
    /**
     * Returns Dispatchers.Default for background CPU computations in production,
     * or Dispatchers.Main if running in a test environment with a TestDispatcher installed.
     */
    val defaultComputation: CoroutineDispatcher
        get() = try {
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
