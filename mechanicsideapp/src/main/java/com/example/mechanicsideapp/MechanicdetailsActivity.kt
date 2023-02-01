package com.example.mechanicsideapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_mechanicdetails.*

class MechanicdetailsActivity : AppCompatActivity() {

    private var mDatabase: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null

    private var userName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mechanicdetails)

        val spinShopLocation = findViewById<View>(R.id.txtShopLocation) as Spinner
        val shopLocation = resources.getStringArray(R.array.shopLocation)
        val adapterShopLocation = ArrayAdapter(this, android.R.layout.simple_spinner_item, shopLocation)
        spinShopLocation.adapter = adapterShopLocation

        val spinExpertise = findViewById<View>(R.id.txtExpertise) as Spinner
        val expertise = resources.getStringArray(R.array.expertise)
        val adapterExpertise = ArrayAdapter(this, android.R.layout.simple_spinner_item, expertise)
        spinExpertise.adapter = adapterExpertise

        val spinExperience = findViewById<View>(R.id.txtExperience) as Spinner
        val exper = resources.getStringArray(R.array.experience)
        val adapterExper = ArrayAdapter(this, android.R.layout.simple_spinner_item, exper)
        spinExperience.adapter = adapterExper

        val spinAvail = findViewById<View>(R.id.txtAvailability) as Spinner
        val avail = resources.getStringArray(R.array.availability)
        val adapterAvail = ArrayAdapter(this, android.R.layout.simple_spinner_item, avail)
        spinAvail.adapter = adapterAvail

        mDatabase = FirebaseDatabase.getInstance().getReference("Mechanics")
        mAuth = FirebaseAuth.getInstance()

        val txtusername = findViewById<View>(R.id.txtUsernameMechanicDetails) as TextView

        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabase!!.child(mUser!!.uid)
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userName = snapshot.child("username").value as String
                if(snapshot.child("username").exists() && userName != ""){
                    txtusername!!.setText("Hi, "+userName)
                }else{
                    txtusername?.setText("Hi, Happy User")
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        val saveDetails = findViewById<View>(R.id.btnContinueSaveDetails) as Button
        saveDetails.setOnClickListener{
            saveMechanicDetails()
        }
    }

    private fun saveMechanicDetails(){
        val fullName = txtFullName.text.toString()
        val mechanicUsername = userName.toString()
        val phoneNumber = txtPhoneNumber.text.toString()
        val shopLocation = txtShopLocation.selectedItem.toString()
        val expertise = txtExpertise.selectedItem.toString()
        val experience = txtExperience.selectedItem.toString()
        val availability = txtAvailability.selectedItem.toString()
        val mUseruid = mAuth!!.uid
        val mUser = mAuth!!.currentUser
        if(fullName.isEmpty() && phoneNumber.isEmpty() && shopLocation.isEmpty() && experience.isEmpty() && expertise.isEmpty() && availability.isEmpty()){
            Toast.makeText(this, "Please enter all details!", Toast.LENGTH_LONG).show()
        }else{
            val mech = Mechanic(mUser!!.uid,mechanicUsername,"",fullName,"",0,"",phoneNumber,shopLocation,expertise,experience,availability)
            mDatabase!!.child(mUser.uid).setValue(mech).addOnCompleteListener{
                val latestActivity = FirebaseDatabase.getInstance().getReference("/latest-activities/$mUseruid").push()
                val activities = Activities(latestActivity.key!!,"You have just updated your details.", mUseruid!!,System.currentTimeMillis()/1000)
                latestActivity.setValue(activities)
                val intent = Intent(this, DashboardmechanicActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                Toast.makeText(this, "Details Saved successfully!", Toast.LENGTH_LONG).show()
            }.addOnFailureListener{ ex ->
                Toast.makeText(this, "Error:" +ex.message.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}