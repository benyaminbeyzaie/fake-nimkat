package com.nimkat.app.repository

import com.google.gson.Gson
import com.nimkat.app.api.NimkatApi
import com.nimkat.app.models.AuthModel
import com.nimkat.app.models.PaginatedResponse
import com.nimkat.app.models.QuestionModel
import com.nimkat.app.utils.AuthPrefs
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyQuestionsRepository @Inject constructor(
    private val api: NimkatApi,
    private val authPrefs: AuthPrefs
) {
    var pageSize = 20;
    var authModel: AuthModel? = null;


    private fun initAuth(): AuthModel? {
        if (authModel != null) return authModel
        val authString = authPrefs.getAuthString()
        if (authString === null) return null
        val gson = Gson()
        authModel = gson.fromJson(authString, AuthModel::class.java)
        return authModel;
    }

    suspend fun getQuestions(page: Int, type: String = "BH", user: String = "me"): Response<PaginatedResponse<List<QuestionModel>>>? {
        initAuth();
        if (authModel == null) return null

        val apiResponse = api.getQuestions(
            "Token ${authModel?.token}", page = page, pageSize = pageSize, type = type, user = user,
        )
        if (apiResponse.body() === null) return null;
        return apiResponse
    }
}