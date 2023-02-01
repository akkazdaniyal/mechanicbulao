package com.example.mechanicapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Users(val uid:String, val username:String, val profileImageUrl:String, val full_name:String, val gender:String, val date_of_birth:String, val phone_number:String, val walletMoney:Int): Parcelable {
    constructor(): this("","","","","","","",0)
}