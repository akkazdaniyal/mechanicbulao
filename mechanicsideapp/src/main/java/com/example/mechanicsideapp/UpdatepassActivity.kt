package com.example.mechanicsideapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UpdatepassActivity : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private lateinit var txtCurrentpass : EditText
    private lateinit var txtNewpass : EditText
    private lateinit var txtRenewpass : EditText
    private lateinit var btnUpdatepass : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_updatepass)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.getReference("Mechanics")
        mAuth = FirebaseAuth.getInstance()

        txtCurrentpass = findViewById<View>(R.id.txtCurrentpassword) as EditText
        txtNewpass = findViewById<View>(R.id.txtNewpassword) as EditText
        txtRenewpass = findViewById<View>(R.id.txtReNewpassword) as EditText
        btnUpdatepass = findViewById<View>(R.id.btnUpdatepassword) as Button

        val backupdatepass = findViewById<View>(R.id.backUpdatepass) as ImageButton
        backupdatepass.setOnClickListener{
            val intent = Intent(this,PersonalinfoActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }
        btnUpdatepass.setOnClickListener{
            updatepass()
        }
    }

    private fun updatepass(){
        val currentPass = txtCurrentpass.text.toString()
        val newPass = txtNewpass.text.toString()
        val reNewpass = txtRenewpass.text.toString()
        val mUseruid = mAuth!!.uid
        val mUser = mAuth!!.currentUser
        val email = mUser?.email.toString()
        val credential = EmailAuthProvider.getCredential(email,currentPass)
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        if(currentPass.isEmpty() && newPass.isEmpty() && reNewpass.isEmpty()){
            Toast.makeText(this, "Please fill up all the fields!", Toast.LENGTH_LONG).show()
        }else{
            mUser.reauthenticate(credential).addOnCompleteListener(this, OnCompleteListener { task ->
                if(task.isSuccessful){
                    if(newPass == reNewpass){
                        mUser.updatePassword(newPass).addOnCompleteListener{
                            Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_LONG).show()
                            val latestActivity = FirebaseDatabase.getInstance().getReference("/latest-activities/$mUseruid").push()
                            val activities = Activities(latestActivity.key!!,"You have just updated your password.", mUseruid!!,System.currentTimeMillis()/1000)
                            latestActivity.setValue(activities)
                        }.addOnFailureListener{ ex ->
                            Toast.makeText(this, "Error:" +ex.message.toString(), Toast.LENGTH_LONG).show()
                        }
                    }else{
                        Toast.makeText(this, "Password does not match!", Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(this, "The current password is incorrect!", Toast.LENGTH_LONG).show()
                }
            })
        }
    }


}