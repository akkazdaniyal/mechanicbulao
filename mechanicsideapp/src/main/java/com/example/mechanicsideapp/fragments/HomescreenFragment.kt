package com.example.mechanicsideapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.mechanicsideapp.*
import com.example.mechanicsideapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class HomescreenFragment : Fragment() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private var tvusername : TextView? = null
    var amount: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Mechanics")
        mAuth = FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_homescreen, container, false)
        tvusername = view.findViewById(R.id.txtUsername) as TextView

        val imgViewImage = view.findViewById<View>(R.id.imgViewImageHomescreen) as ImageView
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val imageLink = snapshot.child("profileImageUrl").value.toString()
                if(snapshot.child("profileImageUrl").exists() && imageLink.isNotEmpty()){
                    Picasso.get().load(imageLink).into(imgViewImage)
                }else{
                    Picasso.get().load(R.drawable.ic_user).into(imgViewImage)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        val txtMoney = view.findViewById<View>(R.id.txtWalletAmountHomescreen) as TextView
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                amount = snapshot.child("walletMoney").getValue(Int::class.java)
                Log.d("Wallet","Money: $amount")
                if(snapshot.child("walletMoney").exists()){
                    txtMoney.setText("Rs. $amount")
                }else{
                    txtMoney.setText("Rs. 0")
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        val imgbtnMessage = view.findViewById<View>(R.id.imgBtnMessage) as ImageButton
        imgbtnMessage.setOnClickListener {
            val intent = Intent(activity, MessagesActivity::class.java)
            startActivity(intent)
        }

        val imgBtnWallet = view.findViewById<View>(R.id.imageButtonWallet) as ImageButton
        imgBtnWallet.setOnClickListener {
            val intent = Intent(activity, WalletActivity::class.java)
            startActivity(intent)
        }
        val linLayWallet = view.findViewById<View>(R.id.lin_layout_wallet) as LinearLayout
        linLayWallet.setOnClickListener {
            val intent = Intent(activity, WalletActivity::class.java)
            startActivity(intent)
        }

        val txtBooking = view.findViewById<View>(R.id.txtBookings) as TextView
        txtBooking.setOnClickListener {
            val intent = Intent(activity, BookingsActivity::class.java)
            startActivity(intent)
        }
        val imgBtnBooking = view.findViewById<View>(R.id.imgbtnBookings) as ImageButton
        imgBtnBooking.setOnClickListener {
            val intent = Intent(activity, BookingsActivity::class.java)
            startActivity(intent)
        }

        val txtService = view.findViewById<View>(R.id.txtServices) as TextView
        txtService.setOnClickListener {
            val intent = Intent(activity, ServicesActivity::class.java)
            startActivity(intent)
        }
        val imgBtnSevice= view.findViewById<View>(R.id.imgbtnServices) as ImageButton
        imgBtnSevice.setOnClickListener {
            val intent = Intent(activity, ServicesActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fullName = snapshot.child("full_name").value as String
                if(snapshot.child("full_name").exists() && fullName != ""){
                    val arr = fullName.split(" ")
                    val fName = arr[0]
                    val lName = arr[1]
                    tvusername!!.setText(fName)
                }else{
                    tvusername?.setText("Happy User")
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }



}