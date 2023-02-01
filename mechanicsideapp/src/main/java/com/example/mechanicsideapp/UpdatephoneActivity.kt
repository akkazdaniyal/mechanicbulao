package com.example.mechanicsideapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UpdatephoneActivity : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private lateinit var txtUpdatephone : EditText
    private lateinit var btnUpdatephone : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_updatephone)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.getReference("Mechanics")
        mAuth = FirebaseAuth.getInstance()

        txtUpdatephone = findViewById<View>(R.id.txtUpdatephone) as EditText
        btnUpdatephone = findViewById<View>(R.id.btnUpdatephone) as Button

        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                txtUpdatephone.setText(snapshot.child("phone_number").value.toString())
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        val backupdatephone = findViewById<View>(R.id.backUpdatephone) as ImageButton
        backupdatephone.setOnClickListener{
            val intent = Intent(this,PersonalinfoActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        btnUpdatephone.setOnClickListener{
            updatephone()
        }

    }

    private fun updatephone(){
        val phone = txtUpdatephone.text.toString()
        val mUseruid = mAuth!!.uid
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        if(phone.isEmpty()){
            Toast.makeText(this, "Please enter phone number!", Toast.LENGTH_LONG).show()
        }else{
            mUserReference.child("phone_number").setValue(phone).addOnCompleteListener{
                Toast.makeText(this, "Phone number updated successfully!", Toast.LENGTH_LONG).show()
                val latestActivity = FirebaseDatabase.getInstance().getReference("/latest-activities/$mUseruid").push()
                val activities = Activities(latestActivity.key!!,"You have just updated your phone number to $phone.", mUseruid!!,System.currentTimeMillis()/1000)
                latestActivity.setValue(activities)
            }.addOnFailureListener{ ex ->
                Toast.makeText(this, "Error:" +ex.message.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}