package com.example.mechanicapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.mechanics_row.view.*

class QuickbookActivity : AppCompatActivity() {

    companion object{
        val MECHANIC_KEY = "MECHANIC_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quickbook)

        val backQuickbook = findViewById<View>(R.id.backQuickbook) as ImageButton
        backQuickbook.setOnClickListener{
            val intent = Intent(this,DashboardActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        fetchMechanics()

    }

    private fun fetchMechanics(){
        val ref = FirebaseDatabase.getInstance().getReference("Mechanics")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val quickbookView = findViewById<View>(R.id.viewQuickbookList) as RecyclerView
                val adapter = GroupieAdapter()
                snapshot.children.forEach{
                    val mech = it.getValue(Mechanic::class.java)
                    if(mech != null){
                        adapter.add(MechanicItemQuickbook(mech))
                    }
                }

                adapter.setOnItemClickListener{item,view->
                    val mechanicItem = item as MechanicItemQuickbook

                    val intent = Intent(view.context,MechanicprofileActivity::class.java)
//                    intent.putExtra(MECHANIC_KEY,mechanicItem.mechanic.Username)
                    intent.putExtra(MECHANIC_KEY,mechanicItem.mechanic)
                    startActivity(intent)

                    finish()
                }

                quickbookView.adapter =adapter
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}

class MechanicItemQuickbook(val mechanic: Mechanic) : Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {3
        val fullName = mechanic?.full_name
        if(fullName != ""){
            val arr = fullName?.split(" ")
            val fName = arr?.get(0)
            val lName = arr?.get(1)
            viewHolder.itemView.txtMechanicName.text = fName
        }else{
            viewHolder.itemView.txtMechanicName.text = "Happy User"
        }

        viewHolder.itemView.txtMechanicExpertise.text = mechanic.shop_location

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