package com.example.mechanicapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*


class UpdatedobActivity : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private lateinit var txtUpdatedob : EditText
    val calender = Calendar.getInstance()
    private lateinit var btnUpdatedob : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_updatedob)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.getReference("Users")
        mAuth = FirebaseAuth.getInstance()

        txtUpdatedob = findViewById<View>(R.id.editTextUpdateDob) as EditText
        btnUpdatedob = findViewById<View>(R.id.btnUpdatedob) as Button

        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                txtUpdatedob.setText(snapshot.child("date_of_birth").value.toString())
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        val date = DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
            calender.set(Calendar.YEAR,year)
            calender.set(Calendar.MONTH, month)
            calender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }

        txtUpdatedob.setOnClickListener(View.OnClickListener {
            DatePickerDialog(
                this,
                date,
                calender.get(Calendar.YEAR),
                calender.get(Calendar.MONTH),
                calender.get(Calendar.DAY_OF_MONTH)
            ).show()
        })

        val backupdatedob = findViewById<View>(R.id.backUpdatedob) as ImageButton
        backupdatedob.setOnClickListener{
            val intent = Intent(this,PersonalinfoActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }
        btnUpdatedob.setOnClickListener {
            updatedob()
        }

    }

    private fun updateLabel() {
        val myFormat = "MM/dd/yy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        txtUpdatedob.setText(dateFormat.format(calender.getTime()))
    }

    private fun updatedob(){
        val dob = txtUpdatedob.text.toString()
        val mUseruid = mAuth!!.uid
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        if(dob.isEmpty()){
            Toast.makeText(this, "Please select date of birth!", Toast.LENGTH_LONG).show()
        }else{
            mUserReference.child("date_of_birth").setValue(dob).addOnCompleteListener{
                Toast.makeText(this, "Date of birth saved successfully!", Toast.LENGTH_LONG).show()
                val latestActivity = FirebaseDatabase.getInstance().getReference("/latest-activities/$mUseruid").push()
                val activities = Activities(latestActivity.key!!,"You have just updated your date of birth to $dob.", mUseruid!!,System.currentTimeMillis()/1000)
                latestActivity.setValue(activities)
            }.addOnFailureListener{ ex ->
                Toast.makeText(this, "Error:" +ex.message.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

}