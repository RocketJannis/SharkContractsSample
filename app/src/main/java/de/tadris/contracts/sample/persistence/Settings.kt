package de.tadris.contracts.sample.persistence

import android.content.Context
import android.preference.PreferenceManager

class Settings(context: Context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    var ownerName
        get() = prefs.getString("owner", "")!!
        set(value) {
            prefs.edit().putString("owner", value).apply()
        }

}