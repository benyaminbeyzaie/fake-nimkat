package com.nimkat.app.repository

import android.util.Log
import com.google.gson.Gson
import com.nimkat.app.api.NimkatApi
import com.nimkat.app.models.*
import com.nimkat.app.utils.AuthPrefs
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: NimkatApi,
    private val authPrefs: AuthPrefs
) {
    fun initAuth(): AuthModel? {
        val authString = authPrefs.getAuthString()
        if (authString === null) return null
        val gson = Gson()
        return gson.fromJson(authString, AuthModel::class.java)
    }

    suspend fun getCode(phoneNumber: String): Response<GetCodeResponse> {
        return api.getCode(GetCodeBody(phoneNumber, "-"))
    }

    suspend fun verifyCode(smsCode: String, id: String): Response<AuthModel>? {
        val apiResponse = api.verifyCode(id, VerifyCodeBody(smsCode))
        if (apiResponse.body() === null) return null;
        Log.d("Auth" , "profile status " + apiResponse.body()!!.isProfileCompleted)
        val gson = Gson()
        authPrefs.setAuthString(gson.toJson(apiResponse.body()))
        Log.d("Auth" , "json is: " + gson.toJson(apiResponse.body()))
        return apiResponse
    }

    fun clearAuth(){
        authPrefs.clearAuth()
    }

    suspend fun updateProfile(name: String , gradeID :Int  , id: String , token: String): Response<ProfileModel>?{

        val apiResponse = api.updateProfile(id , ProfileInfo(name = name , grade = gradeID) ,
            "Token $token"
        )
        if (apiResponse.body() === null) return null;
        Log.d("Auth" , "profile status " + apiResponse.body()!!.isProfileCompleted)
        val gson = Gson()
        authPrefs.setProfileString(gson.toJson(apiResponse.body()))
        Log.d("Auth" , "json is: " + gson.toJson(apiResponse.body()))
        return apiResponse

    }
}