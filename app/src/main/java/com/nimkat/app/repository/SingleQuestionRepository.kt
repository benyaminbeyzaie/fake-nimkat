package com.nimkat.app.repository

import com.google.gson.Gson
import com.nimkat.app.api.NimkatApi
import com.nimkat.app.models.AnswerModel
import com.nimkat.app.models.AuthModel
import com.nimkat.app.models.PaginatedResponse
import com.nimkat.app.utils.AuthPrefs
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SingleQuestionRepository @Inject constructor(
    private val api: NimkatApi,
    private val authPrefs: AuthPrefs
) {
    var authModel: AuthModel? = null

    private fun initAuth(): AuthModel? {
        if (authModel != null) return authModel
        val authString = authPrefs.getAuthString()
        if (authString === null) return null
        val gson = Gson()
        authModel = gson.fromJson(authString, AuthModel::class.java)
        return authModel
    }

    suspend fun getAnswer(
        questionId: Int,
        ): Response<PaginatedResponse<List<AnswerModel>>>? {
        initAuth()
        if (authModel == null) return null

        val apiResponse = api.getAnswer(
            "Token ${authModel?.token}", page = 1, pageSize = 1000, question = questionId,
        )
        if (apiResponse.body() === null) return null
        return apiResponse
    }
}