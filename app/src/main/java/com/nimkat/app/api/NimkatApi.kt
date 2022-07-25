package com.nimkat.app.api

import com.nimkat.app.models.*
import retrofit2.Response
import retrofit2.http.*

interface NimkatApi {
    @POST("users/sign_up/")
    suspend fun getCode(@Body body: GetCodeBody): Response<GetCodeResponse>

    @POST("users/{id}/verify_code/")
    suspend fun verifyCode(@Path("id") id: String, @Body body: VerifyCodeBody): Response<AuthModel>

    @PATCH("users/{id}/")
    suspend fun updateProfile(
        @Path("id") id: String,
        @Body body: ProfileInfo,
        @Header("Authorization") token: String
    ): Response<ProfileModel>

    @GET("users/{id}/")
    suspend fun getProfile(
        @Path("id") id: String,
        @Header("Authorization") token: String
    ): Response<ProfileModel>

    @DELETE("users/{id}/")
    suspend fun deleteAccount(
        @Path("id") id: String,
        @Header("Authorization") token: String
    ): Response<Unit>

    @GET("questions/")
    suspend fun getQuestions(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int,
        @Query("type") type: String,
        @Query("user") user: String
    ): Response<PaginatedResponse<List<QuestionModel>>>

    @GET("answers/")
    suspend fun getAnswer(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int,
        @Query("question") question: Int,
    ): Response<PaginatedResponse<List<AnswerModel>>>

    @POST("devices/")
    suspend fun registerDevice(
        @Header("Authorization") token: String,
        @Body page: RegistrationModel,
    ): Response<RegistrationModel>
}