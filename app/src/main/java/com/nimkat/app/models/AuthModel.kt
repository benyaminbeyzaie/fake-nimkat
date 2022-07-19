package com.nimkat.app.models

import com.google.gson.annotations.SerializedName

data class AuthModel(
    @SerializedName("token")
    var token: String,
    @SerializedName("is_profile_completed")
    var isProfileCompleted: Boolean,
    @SerializedName("is_signed_up")
    var isSignedUp: Boolean,
    @SerializedName("id")
    var userId: Int?,
)

data class GetCodeBody(
    @SerializedName("phone")
    var phone: String,
    @SerializedName("app_code")
    var appCode: String,
)

data class VerifyCodeBody(
    @SerializedName("sms_code")
    var smsCode: String,
)

data class GetCodeResponse(
    @SerializedName("id")
    var smsCode: Int,
)

data class ProfileInfo(

    @SerializedName("first_name")
    var name: String,
    @SerializedName("educational_grade_id")
    var grade: Int
)

data class Profile(

    @SerializedName("first_name")
    var name: String,
    @SerializedName("educational_grade_id")
    var grade: Int,
    @SerializedName("id")
    var userId: Int?,
    @SerializedName("phone")
    var phone: String,
    @SerializedName("educational_grade")
    var educationalGrade: EducationalGrade?,
    @SerializedName("is_profile_completed")
var isProfileCompleted: Boolean,

)

data class EducationalGrade(

    @SerializedName("id") var id: Int?,
    @SerializedName("name") var name: String?,
    @SerializedName("order") var order: Int?

)