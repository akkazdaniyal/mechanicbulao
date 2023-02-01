package com.example.mechanicapp

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

class UpdateemailActivity : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private lateinit var txtUpdateemail : EditText
    private lateinit var btnUpdateemail : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_updateemail)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.getReference("Users")
        mAuth = FirebaseAuth.getInstance()

        txtUpdateemail = findViewById<View>(R.id.editTextUpdateemail) as EditText
        btnUpdateemail = findViewById<View>(R.id.btnUpdateemail) as Button

        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                txtUpdateemail.setText(mUser.email.toString())
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        val backupdateemail = findViewById<View>(R.id.backUpdateemail) as ImageButton
        backupdateemail.setOnClickListener{
            val intent = Intent(this,PersonalinfoActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }
        btnUpdateemail.setOnClickListener{
            updateemail()
        }
    }

    private fun updateemail(){
        val email = txtUpdateemail.text.toString()
        val mUseruid = mAuth!!.uid
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        if(email.isEmpty()){
            Toast.makeText(this, "Please enter email!", Toast.LENGTH_LONG).show()
        }else{
            mUser.updateEmail(email).addOnCompleteListener{
                Toast.makeText(this, "Email address updated successfully!", Toast.LENGTH_LONG).show()
                val latestActivity = FirebaseDatabase.getInstance().getReference("/latest-activities/$mUseruid").push()
                val activities = Activities(latestActivity.key!!,"You have just updated your email.", mUseruid!!,System.currentTimeMillis()/1000)
                latestActivity.setValue(activities)
            }.addOnFailureListener{ ex ->
                Toast.makeText(this, "Error:" +ex.message.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

}