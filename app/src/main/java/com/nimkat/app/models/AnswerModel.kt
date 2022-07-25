package com.nimkat.app.models

import com.google.gson.annotations.SerializedName


data class AnswerModel (

    @SerializedName("user"       ) var user      : User?            = User(),
    @SerializedName("id"         ) var id        : Int?             = null,
    @SerializedName("text"       ) var text      : String?          = null,
    @SerializedName("files"      ) var files     : ArrayList<Files> = arrayListOf(),
    @SerializedName("question"   ) var question  : Int?             = null,
    @SerializedName("is_read"    ) var isRead    : Boolean?         = null,
    @SerializedName("created_at" ) var createdAt : String?          = null

)