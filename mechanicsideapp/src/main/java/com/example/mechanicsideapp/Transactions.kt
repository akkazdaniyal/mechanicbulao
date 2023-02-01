package com.example.mechanicsideapp

class Transactions(val id: String,val transactionType: String,val transactionAmount: Int,val senderId: String,val recieverId: String,val timestamp: Long) {
    constructor() : this("","",0,"","",-1)
}