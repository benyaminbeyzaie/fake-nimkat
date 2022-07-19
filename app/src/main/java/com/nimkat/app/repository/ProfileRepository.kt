package com.nimkat.app.repository

import android.util.Log
import com.google.gson.Gson
import com.nimkat.app.api.NimkatApi
import com.nimkat.app.models.AuthModel
import com.nimkat.app.models.ProfileInfo
import com.nimkat.app.models.ProfileModel
import com.nimkat.app.utils.AuthPrefs
import retrofit2.Response
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val api: NimkatApi,
    private val authPrefs: AuthPrefs
){
    fun initProfile(): ProfileModel? {
        val profileString = authPrefs.getProfileString()
        if (profileString === null) return null
        val gson = Gson()
        return gson.fromJson(profileString, ProfileModel::class.java)
    }

    fun clearAuth(){
        authPrefs.clearAuth()
    }

    suspend fun updateProfile(name: String , gradeID :Int  , id: String): Response<ProfileModel>?{

        val apiResponse = api.updateProfile(id , ProfileInfo(name = name , grade = gradeID))
        if (apiResponse.body() === null) return null;
        Log.d("Auth" , "profile status " + apiResponse.body()!!.isProfileCompleted)
        val gson = Gson()
        authPrefs.setProfileString(gson.toJson(apiResponse.body()))
        Log.d("Auth" , "json is: " + gson.toJson(apiResponse.body()))
        return apiResponse

    }

    suspend fun getProfile(id: String , token: String): Response<ProfileModel>?{
        val apiResponse = api.getProfile(id , token)
        if (apiResponse.body() === null) return null;
        Log.d("Auth" , "profile status " + apiResponse.body()!!.isProfileCompleted)
        val gson = Gson()
        authPrefs.setProfileString(gson.toJson(apiResponse.body()))
        Log.d("Auth" , "json is: " + gson.toJson(apiResponse.body()))
        return apiResponse
    }


}