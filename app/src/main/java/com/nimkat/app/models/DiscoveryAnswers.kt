package com.nimkat.app.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


data class DiscoveryAnswers (
  @SerializedName("id"             ) var id            : Int?              = null,
  @SerializedName("question"       ) var question      : String?           = null,
  @SerializedName("answer"         ) var answer        : String?           = null,
  @SerializedName("question_model" ) var questionModel : Int?              = null,
) : Parcelable {
  constructor(parcel: Parcel) : this(
    parcel.readValue(Int::class.java.classLoader) as? Int,
    parcel.readString(),
    parcel.readString(),
    parcel.readValue(Int::class.java.classLoader) as? Int
  )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeValue(id)
    parcel.writeString(question)
    parcel.writeString(answer)
    parcel.writeValue(questionModel)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<DiscoveryAnswers> {
    override fun createFromParcel(parcel: Parcel): DiscoveryAnswers {
      return DiscoveryAnswers(parcel)
    }

    override fun newArray(size: Int): Array<DiscoveryAnswers?> {
      return arrayOfNulls(size)
    }
  }

}