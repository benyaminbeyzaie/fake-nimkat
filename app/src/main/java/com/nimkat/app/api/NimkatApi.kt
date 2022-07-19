package com.nimkat.app.api

import com.nimkat.app.models.*
import retrofit2.Response
import retrofit2.http.*

interface NimkatApi {
    @POST("users/sign_up/")
    suspend fun getCode(@Body body: GetCodeBody): Response<GetCodeResponse>

    @POST("users/{id}/verify_code/")
    suspend fun verifyCode(@Path("id") id: String, @Body body: VerifyCodeBody): Response<AuthModel>

    @PUT("users/{id}/")
    suspend fun updateProfile(@Path("id") id: String , @Body body: ProfileInfo , @Header("Authorization") token: String): Response<ProfileModel>

    @GET("users/{id}/")
    suspend fun getProfile(@Path("id") id: String  , @Header("Authorization") token: String): Response<ProfileModel>

    @DELETE("users/{id}/")
    suspend fun deleteAccount(@Path("id") id: String  , @Header("Authorization") token: String): Unit
}