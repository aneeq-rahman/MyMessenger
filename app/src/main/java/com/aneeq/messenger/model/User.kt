package com.aneeq.messenger.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(
    val userId:String="",val username:String="",val profileUri:String="",val userEmail:String="",val userStatus:String=""
):Parcelable