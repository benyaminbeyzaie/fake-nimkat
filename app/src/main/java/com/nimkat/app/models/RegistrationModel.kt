package com.nimkat.app.models

import com.google.gson.annotations.SerializedName

data class RegistrationModel(

    @SerializedName("name") var name: String?,
    @SerializedName("registration_id") var registrationId: String,
    @SerializedName("device_id") var deviceId: String?,
    @SerializedName("active") var active: Boolean?,
    @SerializedName("type") var type: String?

)
