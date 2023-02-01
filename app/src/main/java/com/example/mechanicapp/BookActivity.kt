package com.example.mechanicapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_book.*
import kotlinx.android.synthetic.main.activity_mechanicprofile.*
import java.text.SimpleDateFormat
import java.util.*

class BookActivity : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    companion object{
        val MECHANIC_KEY = "MECHANIC_KEY"
    }

    private var currentUser: Users? = null
    private var selectedMechanic: Mechanic? = null
    private lateinit var txtBookingDate : EditText
    private lateinit var txtBookingTime : EditText
    val calender = Calendar.getInstance()
    var fullName: String? = null
    var money: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.getReference("Users")
        mAuth = FirebaseAuth.getInstance()

        selectedMechanic = intent.getParcelableExtra<Mechanic>(MechanicprofileActivity.MECHANIC_KEY)
        txtMechanicNameBook.setText(selectedMechanic?.full_name)

        val backBookme = findViewById<View>(R.id.backBookme) as ImageButton
        backBookme.setOnClickListener{
            val intent = Intent(this,MechanicprofileActivity::class.java)
            intent.putExtra(MECHANIC_KEY,selectedMechanic)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        txtBookingDate = findViewById<View>(R.id.editTextBookingDate) as EditText
        val date = DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
            calender.set(Calendar.YEAR,year)
            calender.set(Calendar.MONTH, month)
            calender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }

        txtBookingDate.setOnClickListener(View.OnClickListener {
            DatePickerDialog(
                this,
                date,
                calender.get(Calendar.YEAR),
                calender.get(Calendar.MONTH),
                calender.get(Calendar.DAY_OF_MONTH)
            ).show()
        })

        txtBookingTime = findViewById<View>(R.id.editTextBookingTime) as EditText

        txtBookingTime.setOnClickListener { // TODO Auto-generated method stub
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mcurrentTime.get(Calendar.MINUTE)
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(
                    timePicker: TimePicker?,
                    selectedHour: Int,
                    selectedMinute: Int
                ) {
                    if (selectedHour > 12) {
                        txtBookingTime.setText(String.format("%02d:%02d PM", selectedHour - 12, selectedMinute))
                    } else {
                        txtBookingTime.setText(String.format("%02d:%02d AM", selectedHour, selectedMinute))
                    }
                }
            }, hour, minute, false)
            mTimePicker.show()
        }

        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                fullName = snapshot.child("full_name").value.toString()
                money = snapshot.child("walletMoney").getValue(Int::class.java)?.toString()
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        val btnCancelBooking = findViewById<View>(R.id.btnCancelBooking) as Button
        btnCancelBooking.setOnClickListener{
            val intent = Intent(this,MechanicprofileActivity::class.java)
            intent.putExtra(MECHANIC_KEY,selectedMechanic)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        val btnConfirmBooking = findViewById<View>(R.id.btnConfirmBooking) as Button
        btnConfirmBooking.setOnClickListener{
            saveBooking()
        }

    }

    private fun saveBooking(){
        val dateBooking = txtBookingDate.text.toString()
        val timeBooking = txtBookingTime.text.toString()
        val userId = FirebaseAuth.getInstance().uid
        val mechanicId = selectedMechanic?.uid
        val mechanicName = selectedMechanic?.full_name
        var walletMoneyMechanic: Int = selectedMechanic!!.walletMoney
        val walletMoneyUser = Integer.parseInt(money)
        if(walletMoneyUser < 0){
            Toast.makeText(this, "Kindly add some funds to your wallet!", Toast.LENGTH_LONG).show()
        }else{
            if(dateBooking.isNotEmpty() && timeBooking.isNotEmpty()){
                val userReference = FirebaseDatabase.getInstance().getReference("/booking-details/$userId").push()
                val mechanicReference = FirebaseDatabase.getInstance().getReference("/booking-details/$mechanicId").push()

                val bookingDetails = BookingDetails(userReference.key!!,
                    userId!!, fullName!!, mechanicId!!, mechanicName!!,dateBooking,timeBooking,System.currentTimeMillis()/1000)
                userReference.setValue(bookingDetails)
                mechanicReference.setValue(bookingDetails)

                val latestActivityUser = FirebaseDatabase.getInstance().getReference("/latest-activities/$userId").push()
                val activitiesUser = Activities(latestActivityUser.key!!,"You have created a new booking for $mechanicName at $dateBooking, $timeBooking",userId,System.currentTimeMillis()/1000)
                latestActivityUser.setValue(activitiesUser)

                val latestActivityMechanic = FirebaseDatabase.getInstance().getReference("/latest-activities/$mechanicId").push()
                val activitiesMechanic = Activities(latestActivityMechanic.key!!,"You have got a new booking for $fullName at $dateBooking, $timeBooking",mechanicId,System.currentTimeMillis()/1000)
                latestActivityMechanic.setValue(activitiesMechanic)

                val newWalletMoneyUser: Int = walletMoneyUser!! - 500
                val mUser = mAuth!!.currentUser
                val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
                mUserReference.child("walletMoney").setValue(newWalletMoneyUser)

                val newwalletMoneyMechanic: Int = walletMoneyMechanic!! + 450
                Log.d("MechanicMoney","curr: $walletMoneyMechanic")
                Log.d("MechanicMoney","new: $newwalletMoneyMechanic")

                Toast.makeText(this, "Booking Successful!", Toast.LENGTH_LONG).show()
                val intent = Intent(this,QuickbookActivity::class.java)
                intent.putExtra(MECHANIC_KEY,selectedMechanic)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
            }else{
                Toast.makeText(this, "Please select date and time!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateLabel() {
        val myFormat = "dd/MM/yyyy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        txtBookingDate.setText(dateFormat.format(calender.getTime()))
    }

}