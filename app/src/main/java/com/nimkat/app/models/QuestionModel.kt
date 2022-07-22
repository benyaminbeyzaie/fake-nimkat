package com.nimkat.app.models

import com.google.gson.annotations.SerializedName


data class QuestionModel (

  @SerializedName("id"                        ) var id                     : Int?                        = null,
  @SerializedName("text"                      ) var text                   : String?                     = null,
  @SerializedName("files"                     ) var files                  : ArrayList<Files>            = arrayListOf(),
  @SerializedName("unread_count"              ) var unreadCount            : Int?                        = null,
  @SerializedName("created_at"                ) var createdAt              : String?                     = null,
  @SerializedName("type"                      ) var type                   : String?                     = null,
  @SerializedName("discovery_answers"         ) var discoveryAnswers       : ArrayList<DiscoveryAnswers> = arrayListOf(),
  @SerializedName("has_answer"                ) var hasAnswer              : String?                     = null,

)