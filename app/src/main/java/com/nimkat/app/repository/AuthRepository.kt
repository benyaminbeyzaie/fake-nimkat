package com.nimkat.app.repository

import android.util.Log
import com.google.gson.Gson
import com.nimkat.app.api.NimkatApi
import com.nimkat.app.models.*
import com.nimkat.app.utils.AuthPrefs
import com.nimkat.app.utils.FirebaseServices
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: NimkatApi,
    private val authPrefs: AuthPrefs,
    private val deviceRepository: DeviceRepository
) {
    var authModel: AuthModel? = null;

    fun initAuth(): AuthModel? {
        if (authModel != null) return authModel
        val authString = authPrefs.getAuthString()
        Log.d("AUTHSTRING" , "Auth string is " + authString)
        if (authString === null) return null
        val gson = Gson()
        authModel = gson.fromJson(authString, AuthModel::class.java)
        GlobalScope.launch {
            deviceRepository.registerDevice()
        }
        return authModel;
    }

    suspend fun getCode(phoneNumber: String): Response<GetCodeResponse> {
        return api.getCode(GetCodeBody(phoneNumber, "-"))
    }

    suspend fun verifyCode(smsCode: String, id: String): Response<AuthModel>? {
        val apiResponse = api.verifyCode(id, VerifyCodeBody(smsCode))
        if (apiResponse.body() === null) return null
        if (!apiResponse.isSuccessful) return null
        Log.d("Auth", "profile status " + apiResponse.body()!!.isProfileCompleted)
        val gson = Gson()
        authPrefs.setAuthString(gson.toJson(apiResponse.body()))
        Log.d("Auth", "json is: " + gson.toJson(apiResponse.body()))
        return apiResponse
    }

    fun clearAuth() {
        authPrefs.clearAuth()
        authModel = null
    }

    fun initProfile(): ProfileModel? {
        val profileString = authPrefs.getProfileString()
        if (profileString === null) return null
        val gson = Gson()
        return gson.fromJson(profileString, ProfileModel::class.java)
    }

    suspend fun updateProfile(
        name: String,
        gradeID: Int,
    ): Response<ProfileModel>? {
        initAuth();
        if (authModel == null) return null
        val apiResponse = api.updateProfile(
            authModel?.userId.toString(), ProfileInfo(name = name, grade = gradeID),
            "Token ${authModel?.token}"
        )
        if (apiResponse.body() === null) return null;
        authModel!!.isProfileCompleted = apiResponse.body()!!.isProfileCompleted
        val gson = Gson()
        authPrefs.setAuthString(gson.toJson(apiResponse.body()))
        Log.d("Auth", "profile status " + apiResponse.body()!!.isProfileCompleted)
        authPrefs.setProfileString(gson.toJson(apiResponse.body()))
        Log.d("Auth", "json is: " + gson.toJson(apiResponse.body()))
        return apiResponse
    }


    suspend fun getProfile(id: String): Response<ProfileModel>? {
        initAuth()
        if (authModel == null) return null
        val apiResponse = api.getProfile(id, "Token ${authModel?.token}")
        if (apiResponse.body() === null) return null;
        Log.d("Auth", "profile status " + apiResponse.body()!!.isProfileCompleted)
        val gson = Gson()
        authPrefs.setProfileString(gson.toJson(apiResponse.body()))
        Log.d("Auth", "json is: " + gson.toJson(apiResponse.body()))
        return apiResponse
    }

    suspend fun delete() {
        initAuth()
        if (authModel == null) return
        api.deleteAccount(authModel!!.userId.toString(), "Token ${authModel!!.token}")
        authPrefs.clearAuth()
        authModel = null
    }

}