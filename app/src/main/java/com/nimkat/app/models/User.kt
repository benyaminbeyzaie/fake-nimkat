package com.nimkat.app.models
import com.google.gson.annotations.SerializedName


data class User (

    @SerializedName("id"                   ) var id                 : Int?               = null,
    @SerializedName("first_name"           ) var firstName          : String?            = null,
    @SerializedName("last_name"            ) var lastName           : String?            = null,
    @SerializedName("username"             ) var username           : String?            = null,
    @SerializedName("is_responder"         ) var isResponder        : Boolean?           = null,
    @SerializedName("photo"                ) var photo              : File?              = null,
    @SerializedName("phone"                ) var phone              : String?            = null,
    @SerializedName("app_version"          ) var appVersion         : String?            = null,
    @SerializedName("is_signed_up"         ) var isSignedUp         : Boolean?           = null,
    @SerializedName("is_profile_completed" ) var isProfileCompleted : Boolean?           = null,
    @SerializedName("coin"                 ) var coin               : Int?               = null,
    @SerializedName("is_admin"             ) var isAdmin            : Boolean?           = null,
    @SerializedName("score"                ) var score              : Int?               = null,
    @SerializedName("show_real_name"       ) var showRealName       : Boolean?           = null,
    @SerializedName("display_name"         ) var displayName        : String?            = null,
    @SerializedName("invite_code"          ) var inviteCode         : String?            = null

)