package com.nimkat.app.models

import com.google.gson.annotations.SerializedName


data class File (

  @SerializedName("attachment"    ) var attachment   : String? = null,
  @SerializedName("original_name" ) var originalName : String? = null,
  @SerializedName("created_at"    ) var createdAt    : String? = null,
  @SerializedName("id"            ) var id           : String? = null

)

data class FileUploadBody(
  @SerializedName("base64"    ) var attachment   : String,
  @SerializedName("original_name" ) var originalName : String,
)