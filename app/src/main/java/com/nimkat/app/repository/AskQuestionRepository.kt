package com.nimkat.app.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.nimkat.app.api.NimkatApi
import com.nimkat.app.models.AskQuestionBody
import com.nimkat.app.models.FileUploadBody
import com.nimkat.app.models.QuestionModel
import com.nimkat.app.utils.AuthPrefs
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AskQuestionRepository @Inject constructor(
    private val api: NimkatApi,
    private val authPrefs: AuthPrefs
) {

    fun isAuth() = authPrefs.initAuth()

    suspend fun sendTextQuestion(question: String): Response<QuestionModel>? {
        val authModel = authPrefs.initAuth() ?: return null
        val apiResponse = api.sendQuestion(
            "Token ${authModel.token}", AskQuestionBody(question, listOf(), "DS", "v1")
        )
        if (apiResponse.body() === null) return null
        return apiResponse
    }

    suspend fun sendImageQuestion(base64: String): Response<QuestionModel>? {
        val authModel = authPrefs.initAuth() ?: return null
        val fileUploadResponse = api.upload("Token ${authModel.token}", FileUploadBody(base64, UUID.randomUUID().toString() + ".jpeg"))
        if (fileUploadResponse.isSuccessful) {
            return api.sendQuestion("Token ${authModel.token}", AskQuestionBody(null, listOf(fileUploadResponse.body()!!.id!!), "DS", "v1"))
        }
        return null
    }

    suspend fun askTeachers(questionId: String): Response<QuestionModel>? {
        val authModel = authPrefs.initAuth() ?: return null
        val apiResponse = api.askTeachers(
            id = questionId,
            AskQuestionBody(null, null, "BH", null), "Token ${authModel.token}",
        )
        if (apiResponse.body() === null) return null
        return apiResponse
    }
}