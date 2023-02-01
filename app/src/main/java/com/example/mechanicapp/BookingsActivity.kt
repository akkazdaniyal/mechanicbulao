package com.example.mechanicapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.bookings_row.view.*
import kotlinx.android.synthetic.main.fragment_activities.*
import kotlinx.android.synthetic.main.mechanics_row.view.*

class BookingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookings)

        val backBookings = findViewById<View>(R.id.backBookings) as ImageButton
        backBookings.setOnClickListener{
            val intent = Intent(this,DashboardActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        fetchBookings()
    }

    private fun fetchBookings(){
        val currentUser = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("booking-details/$currentUser")
        Log.d("Bookings","Ref: $ref")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookingsView = findViewById<View>(R.id.viewBookings) as RecyclerView
                val adapter = GroupieAdapter()
                snapshot.children.forEach{
                    val book = it.getValue(BookingDetails::class.java)
                    Log.d("Bookings","Bookings: $snapshot")
                    if(book != null){
                        adapter.add(Bookings(book))
                    }
                }
                val layoutManager = LinearLayoutManager(application)
                layoutManager.setReverseLayout(true)
                layoutManager.setStackFromEnd(true)
                bookingsView.setLayoutManager(layoutManager)
                bookingsView.adapter =adapter

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}

class Bookings(val bookingDetails: BookingDetails) : Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.txtBookingName.text = bookingDetails.mechanicNameforBooking
        viewHolder.itemView.txtBookingDatenTime.text = bookingDetails.bookingDate+", "+bookingDetails.bookingTime
    }

    override fun getLayout(): Int {
        return R.layout.bookings_row
    }
}