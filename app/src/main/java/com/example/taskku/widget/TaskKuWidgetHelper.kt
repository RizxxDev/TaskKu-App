package com.example.taskku.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object TaskKuWidgetHelper {
    fun updateWidget(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                TaskKuWidget().updateAll(context.applicationContext)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
