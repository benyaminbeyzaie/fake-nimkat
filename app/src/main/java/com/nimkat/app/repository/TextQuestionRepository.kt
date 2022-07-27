package com.nimkat.app.repository

import com.nimkat.app.api.NimkatApi
import com.nimkat.app.callbacks.TextQuestionBody
import com.nimkat.app.callbacks.TextQuestionCallback
import com.nimkat.app.utils.AuthPrefs
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextQuestionRepository @Inject constructor(
    private val api: NimkatApi,
    private val authPrefs: AuthPrefs
) {

    fun isAuth() = authPrefs.initAuth()

    suspend fun getQuestions(question: String): Response<TextQuestionCallback>? {
        val authModel = authPrefs.initAuth() ?: return null
        val apiResponse = api.getTextQuestions(
            "Token ${authModel.token}", TextQuestionBody(question, listOf(), "DS", "v2")
        )
        if (apiResponse.body() === null) return null
        return apiResponse
    }

    suspend fun sendQuestion(question: String): Response<TextQuestionCallback>? {
        val authModel = authPrefs.initAuth() ?: return null
        val apiResponse = api.sendTextQuestions(
            "Token ${authModel.token}", TextQuestionBody(question, listOf(), "DS", "v2")
        )
        if (apiResponse.body() === null) return null
        return apiResponse
    }
}