package com.example.mechanicsideapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PersonalinfoActivity : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personalinfo)


        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Mechanics")
        mAuth = FirebaseAuth.getInstance()

        val imgUpdatename = findViewById(R.id.imgBtnUpdateName) as ImageButton
        val txtUpdatename = findViewById(R.id.txtUpdateName) as TextView
        val imgUpdatenameArrow = findViewById(R.id.imgBtnUpdateNameArrow) as ImageButton

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

        imgUpdatename.setOnClickListener{
            val intent = Intent(this, UpdatenameActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
        txtUpdatename.setOnClickListener{
            val intent = Intent(this, UpdatenameActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
        imgUpdatenameArrow.setOnClickListener{
            val intent = Intent(this, UpdatenameActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }

        val imgUpdateimage = findViewById(R.id.imgBtnUpdateImage) as ImageButton
        val txtUpdateimage = findViewById(R.id.txtUpdateImage) as TextView
        val imgUpdateimageArrow = findViewById(R.id.imgBtnUpdateImageArrow) as ImageButton
        imgUpdateimage.setOnClickListener{
            val intent = Intent(this, UpdateimageActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
        txtUpdateimage.setOnClickListener{
            val intent = Intent(this, UpdateimageActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
        imgUpdateimageArrow.setOnClickListener{
            val intent = Intent(this, UpdateimageActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }

        val imgUpdatephone = findViewById(R.id.imgBtnUpdatePhone) as ImageButton
        val txtUpdatephone = findViewById(R.id.txtUpdatePhone) as TextView
        val imgUpdatephoneArrow = findViewById(R.id.imgBtnUpdatePhoneArrow) as ImageButton
        imgUpdatephone.setOnClickListener{
            val intent = Intent(this, UpdatephoneActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
        txtUpdatephone.setOnClickListener{
            val intent = Intent(this, UpdatephoneActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
        imgUpdatephoneArrow.setOnClickListener{
            val intent = Intent(this, UpdatephoneActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }

        val imgUpdateemail = findViewById(R.id.imgBtnUpdateEmail) as ImageButton
        val txtUpdateemail = findViewById(R.id.txtUpdateEmail) as TextView
        val imgUpdateemailArrow = findViewById(R.id.imgBtnUpdateEmailArrow) as ImageButton
        imgUpdateemail.setOnClickListener{
            val intent = Intent(this, UpdateemailActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
        txtUpdateemail.setOnClickListener{
            val intent = Intent(this, UpdateemailActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
        imgUpdateemailArrow.setOnClickListener{
            val intent = Intent(this, UpdateemailActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }

        val imgUpdatepass = findViewById(R.id.imgBtnUpdatePass) as ImageButton
        val txtUpdatepass = findViewById(R.id.txtUpdatePass) as TextView
        val imgUpdatepassArrow = findViewById(R.id.imgBtnUpdatePassArrow) as ImageButton
        imgUpdatepass.setOnClickListener{
            val intent = Intent(this, UpdatepassActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
        txtUpdatepass.setOnClickListener{
            val intent = Intent(this, UpdatepassActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
        imgUpdatepassArrow.setOnClickListener{
            val intent = Intent(this, UpdatepassActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }

        val imgUpdategender = findViewById(R.id.imgBtnUpdateGender) as ImageButton
        val txtUpdategender = findViewById(R.id.txtUpdateGender) as TextView
        val imgUpdategenderArrow = findViewById(R.id.imgBtnUpdateGenderArrow) as ImageButton
        imgUpdategender.setOnClickListener{
            val intent = Intent(this, UpdategenderActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
        txtUpdategender.setOnClickListener{
            val intent = Intent(this, UpdategenderActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
        imgUpdategenderArrow.setOnClickListener{
            val intent = Intent(this, UpdategenderActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }

        val imgUpdatedob = findViewById(R.id.imgBtnUpdateDOB) as ImageButton
        val txtUpdatedob = findViewById(R.id.txtUpdateDOB) as TextView
        val imgUpdatedobArrow = findViewById(R.id.imgBtnUpdateDOBArrow) as ImageButton
        imgUpdatedob.setOnClickListener{
            val intent = Intent(this, UpdatedobActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
        txtUpdatedob.setOnClickListener{
            val intent = Intent(this, UpdatedobActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
        imgUpdatedobArrow.setOnClickListener{
            val intent = Intent(this, UpdatedobActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }

        val backperonalinfo = findViewById<View>(R.id.backPersonalinfo) as ImageButton
        backperonalinfo.setOnClickListener{
            val intent = Intent(this,DashboardmechanicActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }
    }

}