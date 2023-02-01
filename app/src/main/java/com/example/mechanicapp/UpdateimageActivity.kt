package com.example.mechanicapp

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_updateimage.*
import java.util.*

class UpdateimageActivity : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_updateimage)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.getReference("Users")
        mAuth = FirebaseAuth.getInstance()

        val imgviewupdateImage = findViewById<View>(R.id.imgViewUpdateImage) as ImageView
        imgviewupdateImage.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }

        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val imageLink = snapshot.child("profileImageUrl").value.toString()
                if(snapshot.child("profileImageUrl").exists() && imageLink.isNotEmpty()){
                    Picasso.get().load(imageLink).into(imgviewupdateImage)
                }else{
                    Picasso.get().load(R.drawable.ic_user).into(imgviewupdateImage)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        val backupdateimage = findViewById<View>(R.id.backUpdateimage) as ImageButton
        backupdateimage.setOnClickListener{
            val intent = Intent(this,PersonalinfoActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }
        val btnupdateImage = findViewById<View>(R.id.btnUpdateimage) as Button
        btnupdateImage.setOnClickListener{
            uploadImagetoFirebaseStorage()
        }
    }

    var selectedImageUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==0 && resultCode == RESULT_OK && data != null){
            selectedImageUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedImageUri)
            imgViewUpdateImage.setImageBitmap(bitmap)
//            val bitmapDrawable = BitmapDrawable(bitmap)
//            imgViewUpdateImage.setImageDrawable(bitmapDrawable)
        }
    }

    private fun uploadImagetoFirebaseStorage(){
        if(selectedImageUri == null) {
            Toast.makeText(this, "Please select a profile image!", Toast.LENGTH_LONG).show()
        }
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        val mUseruid = mAuth!!.uid
        ref.putFile(selectedImageUri!!)
            .addOnCompleteListener{
                ref.downloadUrl.addOnSuccessListener {
                    updateImage(it.toString())
                    Toast.makeText(this, "Profile image updated successfully!", Toast.LENGTH_LONG).show()
                    val latestActivity = FirebaseDatabase.getInstance().getReference("/latest-activities/$mUseruid").push()
                    val activities = Activities(latestActivity.key!!,"You have just updated your profile image.", mUseruid!!,System.currentTimeMillis()/1000)
                    latestActivity.setValue(activities)
                }.addOnFailureListener{ex ->
                    Toast.makeText(this, "Error:" +ex.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun updateImage(profileImageUrl: String){
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
            mUserReference.child("profileImageUrl").setValue(profileImageUrl)
    }
}
