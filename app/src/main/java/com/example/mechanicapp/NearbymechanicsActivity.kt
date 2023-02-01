package com.example.mechanicapp

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.mechanics_row.view.*

class NearbymechanicsActivity : AppCompatActivity() {

    companion object{
        val MECHANIC_KEY = "MECHANIC_KEY"
        var loggedInUser : Users? = null
    }

    var itemsSelected : ArrayList<Any>? = null
    var selectedLocationIndex : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearbymechanics)

        val mechanicsView = findViewById<View>(R.id.viewMechanics) as RecyclerView

//        val adapter = GroupieAdapter()
//        adapter.add(MechanicItem())
//        adapter.add(MechanicItem())
//        adapter.add(MechanicItem())
//        mechanicsView.adapter = adapter

        val backnearMechanics = findViewById<View>(R.id.backNearbyMechanics) as ImageButton
        backnearMechanics.setOnClickListener{
            val intent = Intent(this,DashboardActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        val imgBtnSelectlocation = findViewById<View>(R.id.imgBtnSelectLocation) as ImageButton
        imgBtnSelectlocation.setOnClickListener{
            selectLocationDialog()
        }

        val txtSelectlocation = findViewById<View>(R.id.txtSelectLocation) as TextView
        txtSelectlocation.setOnClickListener{
            selectLocationDialog()
        }

        fetchCurrentUser()
        fetchMechanics()
    }

    private fun selectLocationDialog(){

        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("AlertDialog")
        val items = resources.getStringArray(R.array.shopLocation)
        val itemsSort = items.sortedBy { it.length }
        var selectedLocation : String? = null
        alertDialog.setSingleChoiceItems(
            items, selectedLocationIndex
        ) { dialog, which ->
            selectedLocationIndex = which
            selectedLocation = items[selectedLocationIndex]
        }


        alertDialog.setPositiveButton("Ok", DialogInterface.OnClickListener {
            dialog, which ->
            Log.d("MechanicName","$selectedLocationIndex => $selectedLocation")
                Log.d("MechanicName","Java Clicked: $selectedLocation")
                val ref = FirebaseDatabase.getInstance().getReference("Mechanics")
                ref.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val mechanicsView = findViewById<View>(R.id.viewMechanics) as RecyclerView
                        val adapter = GroupieAdapter()
                        snapshot.children.forEach{
                            Log.d("NewMechanic",toString())
                            val mech = it.getValue(Mechanic::class.java)
                            if(mech != null && selectedLocationIndex==0){
                                adapter.add(MechanicItem(mech))
                            }else if(mech != null && mech.shop_location==selectedLocation){
                                adapter.add(MechanicItem(mech))
                            }
                        }

                        adapter.setOnItemClickListener{item,view->

                            val mechanicItem = item as MechanicItem

                            val intent = Intent(view.context,MechanicprofileActivity::class.java)
//                    intent.putExtra(MECHANIC_KEY,mechanicItem.mechanic.Username)
                            intent.putExtra(MECHANIC_KEY,mechanicItem.mechanic)
                            startActivity(intent)

                            finish()
                        }

                        mechanicsView.adapter =adapter
                    }
                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        })
        val alert = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()

//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Select location")
//        val locations = arrayOf("Saddar", "Clifton", "Shahrah-e-faisal", "Liaquatabad", " North Nazimabad")
//
//        builder.setMultiChoiceItems(locations,null,object : OnMultiChoiceClickListener{
//            override fun onClick(dialog: DialogInterface?, which: Int, isChecked: Boolean) {
//                if(isChecked){
//                    itemsSelected?.add(which)
//                }else if(itemsSelected!!.contains(which)){
//                    itemsSelected?.remove(which)
//                }
//            }
//        })
//
//        builder.setPositiveButton("Ok",object : OnClickListener{
//            override fun onClick(dialog: DialogInterface?, which: Int) {
//
//            }
//        })
//
//        builder.setNegativeButton("Cancel",object : OnClickListener{
//            override fun onClick(dialog: DialogInterface?, which: Int) {
//                TODO("Not yet implemented")
//            }
//        })
//
//        builder.show()
    }

    private fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref= FirebaseDatabase.getInstance().getReference("/Users/$uid")
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                loggedInUser = snapshot.getValue(Users::class.java)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun fetchMechanics(){
        val ref = FirebaseDatabase.getInstance().getReference("Mechanics")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val mechanicsView = findViewById<View>(R.id.viewMechanics) as RecyclerView
                val adapter = GroupieAdapter()
                snapshot.children.forEach{
                    Log.d("NewMechanic",toString())
                    val mech = it.getValue(Mechanic::class.java)
                    if(mech != null){
                        adapter.add(MechanicItem(mech))
                    }
                }

                adapter.setOnItemClickListener{item,view->

                    val mechanicItem = item as MechanicItem

                    val intent = Intent(view.context,MechanicprofileActivity::class.java)
//                    intent.putExtra(MECHANIC_KEY,mechanicItem.mechanic.Username)
                    intent.putExtra(MECHANIC_KEY,mechanicItem.mechanic)
                    startActivity(intent)

                    finish()
                }

                mechanicsView.adapter =adapter
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}

class MechanicItem(val mechanic: Mechanic) : Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        val fullName = mechanic?.full_name
        if(fullName != ""){
            val arr = fullName?.split(" ")
            val fName = arr?.get(0)
            val lName = arr?.get(1)
            viewHolder.itemView.txtMechanicName.text = fName
        }else{
            viewHolder.itemView.txtMechanicName.text = "Happy User"
        }

        viewHolder.itemView.txtMechanicExpertise.text = mechanic.expertise

        val uri = mechanic.profileImageUrl
        val targetImageview = viewHolder.itemView.imgViewMechanic
        if(uri!!.isEmpty()){
            Picasso.get().load(R.drawable.ic_user).into(targetImageview)
        }else{
            Picasso.get().load(uri).into(targetImageview)
        }
    }

    override fun getLayout(): Int {
        return R.layout.mechanics_row
    }
}