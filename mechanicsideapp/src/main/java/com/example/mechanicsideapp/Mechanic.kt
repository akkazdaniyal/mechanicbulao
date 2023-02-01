package com.example.mechanicsideapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Mechanic(val uid:String, val username:String, val profileImageUrl:String, val full_name:String, val gender:String, val walletMoney:Int, val date_of_birth:String, val phone_number:String, val shop_location:String,val expertise:String, val experience:String, val availability:String): Parcelable {
    constructor() : this("","","","","",0,"","","","","","")
}