package com.nimkat.app.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class QuestionModel : Serializable {

  @SerializedName("id"                        ) var id                     : Int?                        = null
  @SerializedName("text"                      ) var text                   : String?                     = null
  @SerializedName("files"                     ) var files                  : ArrayList<Files>            = arrayListOf()
  @SerializedName("unread_count"              ) var unreadCount            : Int?                        = null
  @SerializedName("created_at"                ) var createdAt              : String?                     = null
  @SerializedName("type"                      ) var type                   : String?                     = null
  @SerializedName("discovery_answers"         ) var discoveryAnswers       : ArrayList<DiscoveryAnswers> = arrayListOf()
  @SerializedName("has_answer"                ) var hasAnswer              : String?                     = null

}

data class AskQuestionBody(
  @SerializedName("text") val text : String?,
  @SerializedName("files") val files : List<FileIdBody>?,
  @SerializedName("type") val type : String,
  @SerializedName("engine_version") val engine_version : String?,
)

data class FileIdBody(
  @SerializedName("file_id") val fileId : String?,
  @SerializedName("order") val order : Int?,
)
