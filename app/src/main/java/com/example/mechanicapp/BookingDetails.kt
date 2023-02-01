package com.example.mechanicapp

class BookingDetails(val id: String,val userforBooking: String,val userNameforBooking: String,val mechanicforBooking: String,val mechanicNameforBooking: String,val bookingDate: String,val bookingTime: String,val timestamp: Long) {
    constructor() : this("","","","","","","",-1)
}