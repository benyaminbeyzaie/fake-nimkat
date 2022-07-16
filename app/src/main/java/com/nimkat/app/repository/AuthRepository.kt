package com.nimkat.app.repository

import com.nimkat.app.api.NimkatApi
import com.nimkat.app.models.AuthModel
import com.nimkat.app.models.GetCodeBody
import com.nimkat.app.models.GetCodeResponse
import com.nimkat.app.models.VerifyCodeBody
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor(private val api: NimkatApi) {

    private var _lastCalledId = ""

    suspend fun getCode(phoneNumber: String): Response<GetCodeResponse> {
        val apiResponse = api.getCode(GetCodeBody(phoneNumber, "-"))
        if (apiResponse.isSuccessful) {
            _lastCalledId = apiResponse.body()!!.smsCode.toString()
        }
        return apiResponse

    }

    suspend fun verifyCode(smsCode: String): Response<AuthModel>? {
        if (_lastCalledId.isEmpty()) {
            return null
        }
        val apiResponse = api.verifyCode(_lastCalledId, VerifyCodeBody(smsCode))
        _lastCalledId = ""
        return apiResponse
    }

}