package com.example.mechanicsideapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UpdategenderActivity : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private lateinit var txtUpdategender : Spinner
    private lateinit var btnUpdategender : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_updategender)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.getReference("Mechanics")
        mAuth = FirebaseAuth.getInstance()

        txtUpdategender = findViewById<View>(R.id.spinnerUpdategender) as Spinner
        btnUpdategender = findViewById<View>(R.id.btnUpdategender) as Button

        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        val backupdategender = findViewById<View>(R.id.backUpdategender) as ImageButton
        backupdategender.setOnClickListener {
            val intent = Intent(this, PersonalinfoActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        val spinGender = findViewById<View>(R.id.spinnerUpdategender) as Spinner
        val gender = resources.getStringArray(R.array.gender)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, gender)
        spinGender.adapter = adapter

        btnUpdategender.setOnClickListener {
            updategender()
        }

    }

    private fun updategender(){
        val gender = txtUpdategender.selectedItem.toString()
        val mUseruid = mAuth!!.uid
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        if(gender.isEmpty()){
            Toast.makeText(this, "Please select gender!", Toast.LENGTH_LONG).show()
        }else{
            mUserReference.child("gender").setValue(gender).addOnCompleteListener{
                Toast.makeText(this, "Gender saved successfully!", Toast.LENGTH_LONG).show()
                val latestActivity = FirebaseDatabase.getInstance().getReference("/latest-activities/$mUseruid").push()
                val activities = Activities(latestActivity.key!!,"You have just updated your gender to $gender.", mUseruid!!,System.currentTimeMillis()/1000)
                latestActivity.setValue(activities)
            }.addOnFailureListener{ ex ->
                Toast.makeText(this, "Error:" +ex.message.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

}