package com.example.mechanicsideapp

class ChatMessage(val id: String,val text: String,val senderId: String,val recieverId: String,val timestamp: Long){
    constructor() : this("","","","",-1)
}