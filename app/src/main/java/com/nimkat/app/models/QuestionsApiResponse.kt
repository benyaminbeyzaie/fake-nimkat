package com.nimkat.app.models

import com.google.gson.annotations.SerializedName

data class QuestionsApiResponse(
    @SerializedName("results") var results: ArrayList<QuestionModel> = arrayListOf(),
)
