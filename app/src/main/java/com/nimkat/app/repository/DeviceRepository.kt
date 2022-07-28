package com.nimkat.app.repository

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.nimkat.app.api.NimkatApi
import com.nimkat.app.models.AuthModel
import com.nimkat.app.models.RegistrationModel
import com.nimkat.app.utils.AuthPrefs
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DeviceRepository @Inject constructor(
    private val api: NimkatApi,
    private val authPrefs: AuthPrefs
) {
    var authModel: AuthModel? = null;


    private fun initAuth(): AuthModel? {
        if (authModel != null) return authModel
        val authString = authPrefs.getAuthString()
        if (authString === null) return null
        val gson = Gson()
        authModel = gson.fromJson(authString, AuthModel::class.java)
        return authModel;
    }

    suspend fun registerDevice(firebaseToken: String): Response<RegistrationModel>? {
        initAuth()

        if (authModel == null) return null
        var apiResponse: Response<RegistrationModel>? = null

        apiResponse = api.registerDevice(
            "Token ${authModel?.token}",
            RegistrationModel(
                active = true,
                registrationId = firebaseToken,
                type = "android",
                deviceId = UUID.randomUUID().toString(),
                name = "name"
            )
        )

        if (apiResponse == null || apiResponse!!.body() === null) return null;
        return apiResponse
    }
}