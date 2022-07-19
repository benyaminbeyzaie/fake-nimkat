package com.nimkat.app.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthPrefs @Inject constructor(@ApplicationContext context : Context){
    private val authPrefTag = "auth_tag";
    private val authProfileTag = "profile_tag"
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun getAuthString(): String? {
        val authString = prefs.getString(authPrefTag, "");

        return authString;
    }

    fun setAuthString(query: String) {
        prefs.edit().putString(authPrefTag, query).apply()
    }

    fun setProfileString(query: String){
        prefs.edit().putString(authProfileTag , query).apply()
    }

    fun clearAuth() {
        prefs.edit().remove(authPrefTag).apply()
        prefs.edit().remove(authProfileTag).apply()
    }
}