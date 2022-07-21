package com.nimkat.app.models

import com.google.gson.annotations.SerializedName


data class DiscoveryAnswers (
  @SerializedName("id"             ) var id            : Int?              = null,
  @SerializedName("question"       ) var question      : String?           = null,
  @SerializedName("answer"         ) var answer        : String?           = null,
  @SerializedName("question_model" ) var questionModel : Int?              = null,
)