package edu.ucne.doers

import android.content.Context

class AppReferences(contexto: Context) {
    private val sharedPref = contexto.getSharedPreferences(
        "app_prefs", Context.MODE_PRIVATE
    )

    fun isFirstTime(): Boolean {
        return sharedPref.getBoolean("is_first_time", true)
    }

    fun setFirstTimeCompleted() {
        sharedPref.edit().putBoolean("is_first_time", false).apply()
    }
}