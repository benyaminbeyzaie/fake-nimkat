package com.nimkat.app.models

import com.google.gson.annotations.SerializedName


data class Files (

  @SerializedName("id"       ) var id       : Int?  = null,
  @SerializedName("question" ) var question : Int?  = null,
  @SerializedName("file"     ) var file     : File? = File(),
  @SerializedName("order"    ) var order    : Int?  = null

)