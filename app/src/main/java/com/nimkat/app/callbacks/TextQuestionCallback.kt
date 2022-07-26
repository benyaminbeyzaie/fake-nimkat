package com.nimkat.app.callbacks

import com.google.gson.annotations.SerializedName
import com.nimkat.app.models.DiscoveryAnswers

data class TextQuestionCallback(
    val discovery_answers: ArrayList<DiscoveryAnswers>?
) {
}


data class TextQuestionBody(
    @SerializedName("text") val text : String,
    @SerializedName("files") val files : List<String>,
    @SerializedName("type") val type : String,
    @SerializedName("engine_version") val engine_version : String,
)