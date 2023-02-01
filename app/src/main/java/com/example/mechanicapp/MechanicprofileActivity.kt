package com.example.mechanicapp

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_mechanicprofile.*
import kotlinx.android.synthetic.main.mechanics_row.view.*

class MechanicprofileActivity : AppCompatActivity() {

    companion object{
        val MECHANIC_KEY = "MECHANIC_KEY"
    }

    private var selectedMechanic: Mechanic? = null
    private var PHONE_PERMISSION_CODE: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mechanicprofile)

        val backMechanicProfile = findViewById<View>(R.id.backMechanicProfile) as ImageButton
        backMechanicProfile.setOnClickListener{
            val intent = Intent(this,QuickbookActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        selectedMechanic = intent.getParcelableExtra<Mechanic>(QuickbookActivity.MECHANIC_KEY)
        if(selectedMechanic != null){
            selectedMechanic = intent.getParcelableExtra<Mechanic>(QuickbookActivity.MECHANIC_KEY)
        }
        else{
            selectedMechanic = intent.getParcelableExtra<Mechanic>(BookActivity.MECHANIC_KEY)
        }
        txtMechanicNameProfile.setText(selectedMechanic?.full_name)
        txtMechanicPhoneNumber.setText(selectedMechanic?.phone_number)
        txtMechanicAvailability.setText(selectedMechanic?.availability)
        txtMechanicAreasCovering.setText(selectedMechanic?.shop_location)
        txtMechanicShopLocation.setText(selectedMechanic?.phone_number)
        txtMechanicExperience.setText(selectedMechanic?.experience)
        txtMechanicExpertise.setText(selectedMechanic?.expertise)

        val uri = selectedMechanic?.profileImageUrl
        if(uri!!.isEmpty()){
            Picasso.get().load(R.drawable.ic_user).into(imgViewMechnaicImageProfile)
        }else{
            Picasso.get().load(uri).into(imgViewMechnaicImageProfile)
        }

        val btnMessage = findViewById<View>(R.id.btnMessageMechanicProfile) as Button
        btnMessage.setOnClickListener{
            val intent = Intent(this,ChatlogActivity::class.java)
            intent.putExtra(MECHANIC_KEY,selectedMechanic)
            startActivity(intent)
        }

        val btnCall = findViewById<View>(R.id.btnCallMechanicProfile) as Button
        btnCall.setOnClickListener{
            checkPhonePermissionandMakeCall()
        }

        val btnBook = findViewById<View>(R.id.btnBookMechanicProfile) as Button
        btnBook.setOnClickListener{
            val intent = Intent(this,BookActivity::class.java)
            intent.putExtra(MECHANIC_KEY,selectedMechanic)
            startActivity(intent)
        }

    }

    private fun makephoneCall(phoneNumber: String){
        val intent = Intent(Intent.ACTION_CALL)
        intent.setData(Uri.parse("tel:"+phoneNumber))
        startActivity(intent)
    }
    
    private fun checkPhonePermissionandMakeCall() {
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE),PHONE_PERMISSION_CODE)
        }else{
            val number = selectedMechanic?.phone_number.toString()
            makephoneCall(""+number)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PHONE_PERMISSION_CODE){
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val number = selectedMechanic?.phone_number.toString()
                makephoneCall(""+number)
            }else{
                Toast.makeText(this, "Phone Call Permission denied, Please allow permission to make a call!",Toast.LENGTH_LONG).show()
            }
        }
    }
}