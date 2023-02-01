package com.example.mechanicsideapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UpdatenameActivity : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private lateinit var txtUpdatename : EditText
    private lateinit var btnUpdatename : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_updatename)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.getReference("Mechanics")
        mAuth = FirebaseAuth.getInstance()

        txtUpdatename = findViewById<View>(R.id.editTextUpdatename) as EditText
        btnUpdatename = findViewById<View>(R.id.btnUpdatename) as Button

        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fullName = snapshot.child("full_name").value.toString()
                if(snapshot.child("full_name").exists() && fullName != ""){
                    txtUpdatename.setText(fullName)
                }else{
                    txtUpdatename.setText("Happy User")
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        val backupdatename = findViewById<View>(R.id.backUpdatename) as ImageButton
        backupdatename.setOnClickListener{
            val intent = Intent(this,PersonalinfoActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        btnUpdatename.setOnClickListener{
            updatename()
        }

    }

    private fun updatename(){
        val name = txtUpdatename.text.toString()
        val mUseruid = mAuth!!.uid
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        if(name.isEmpty()){
            Toast.makeText(this, "Please enter name!", Toast.LENGTH_LONG).show()
        }else{
            mUserReference.child("full_name").setValue(name).addOnCompleteListener{
                val latestActivity = FirebaseDatabase.getInstance().getReference("/latest-activities/$mUseruid").push()
                val activities = Activities(latestActivity.key!!,"You have just updated your name to $name.", mUseruid!!,System.currentTimeMillis()/1000)
                latestActivity.setValue(activities)
                Toast.makeText(this, "Name updated successfully!", Toast.LENGTH_LONG).show()
            }.addOnFailureListener{ ex ->
                Toast.makeText(this, "Error:" +ex.message.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}