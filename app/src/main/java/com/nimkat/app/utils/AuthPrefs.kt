package com.nimkat.app.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.nimkat.app.models.AuthModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthPrefs @Inject constructor(@ApplicationContext context : Context){
    private val authPrefTag = "auth_tag";
    private val profileTag = "profile_tag"
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun getAuthString(): String? {
        val authString = prefs.getString(authPrefTag, "");

        return authString;
    }

    fun getProfileString(): String? {
        val authString = prefs.getString(profileTag, "");

        return authString;
    }

    fun setAuthString(query: String) {
        prefs.edit().putString(authPrefTag, query).apply()
    }

    fun setProfileString(query: String){
        prefs.edit().putString(profileTag , query).apply()
    }

    fun initAuth(): AuthModel? {
        val authString = getAuthString()
        if (authString === null) return null
        val gson = Gson()
        return gson.fromJson(authString, AuthModel::class.java)
    }

    fun clearAuth() {
        prefs.edit().remove(authPrefTag).apply()
        prefs.edit().remove(profileTag).apply()
    }
}