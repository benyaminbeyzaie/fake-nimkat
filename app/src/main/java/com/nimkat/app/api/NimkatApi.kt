package com.nimkat.app.api

import com.nimkat.app.models.AuthModel
import com.nimkat.app.models.GetCodeBody
import com.nimkat.app.models.GetCodeResponse
import com.nimkat.app.models.VerifyCodeBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface NimkatApi {
    @POST("users/sign_up/")
    suspend fun getCode(@Body body: GetCodeBody): Response<GetCodeResponse>

    @POST("users/{id}/verify_code/")
    suspend fun verifyCode(@Path("id") id: String, @Body body: VerifyCodeBody): Response<AuthModel>
}