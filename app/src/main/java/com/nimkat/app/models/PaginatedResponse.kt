package com.nimkat.app.models

import com.google.gson.annotations.SerializedName

data class PaginatedResponse<T>(
    @SerializedName("results") var results: T,
    @SerializedName("next") var next: String?,
)
