package com.example.mechanicapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.Navigation
import com.example.mechanicapp.LoginActivity
import com.example.mechanicapp.PersonalinfoActivity
import com.example.mechanicapp.R
import com.example.mechanicapp.WalletActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.w3c.dom.Text

class ProfileFragment : Fragment() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private var txtusername : TextView? = null
    private var txtLogout : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        mAuth = FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        txtusername = view.findViewById(R.id.txtUsernameProfile) as TextView
        txtLogout = view.findViewById(R.id.txtLogOut) as TextView

        txtLogout!!.setOnClickListener{
            mAuth?.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

        val imgPersonalinfo = view.findViewById(R.id.imgBtnPersonalinfo) as ImageButton
        val txtPersonalinfo = view.findViewById(R.id.txtPersonalinfo) as TextView
        val imgPersonalinfoArrow = view.findViewById(R.id.imgBtnPersonalinfoArrow) as ImageButton

        imgPersonalinfo.setOnClickListener{
            val intent = Intent(activity, PersonalinfoActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
        txtPersonalinfo.setOnClickListener{
            val intent = Intent(activity, PersonalinfoActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
        imgPersonalinfoArrow.setOnClickListener{
            val intent = Intent(activity, PersonalinfoActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fullName = snapshot.child("full_name").value.toString()
                if(snapshot.child("full_name").exists() && fullName != ""){
                    val arr = fullName.split(" ")
                    val fName = arr[0]
                    val lName = arr[1]
                    txtusername!!.setText(fName)
                }else{
                    txtusername?.setText("Happy User")
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

}