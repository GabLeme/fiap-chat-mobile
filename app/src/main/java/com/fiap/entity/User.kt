package com.fiap.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
    var uuid: String = "",
    var email: String = "",
    var uriProfile: String = ""
): Parcelable {

}