package com.example.mechanicapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mechanicapp.Activities
import com.example.mechanicapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activities_row.view.*
import kotlinx.android.synthetic.main.fragment_activities.*
import kotlinx.android.synthetic.main.messages_row.view.*

class ActivitiesFragment : Fragment() {

    var activ : Activities? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_activities, container, false)

        val activityView = view.findViewById<View>(R.id.viewActivities) as RecyclerView

//        val adapter = GroupieAdapter()
//        adapter.add(LatestActivity())
//        adapter.add(LatestActivity())
//        adapter.add(LatestActivity())
//        adapter.add(LatestActivity())
//        activityView.adapter = adapter
        listenforLatestActivity()

        return view
    }

    private fun listenforLatestActivity(){
        val currentUser = FirebaseAuth.getInstance().uid
        val actId = activ?.id
        val ref = FirebaseDatabase.getInstance().getReference("/latest-activities/$currentUser")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupieAdapter()
                snapshot.children.forEach{
                    val activity = it.getValue(Activities::class.java)
                    val reci = activity?.recieverId.toString()
                    if(activity!=null && activity.recieverId==currentUser){
                        adapter.add(LatestActivity(activity))
                    }
                }
                val layoutManager = LinearLayoutManager(activity)
                layoutManager.setReverseLayout(true)
                layoutManager.setStackFromEnd(true)
                viewActivities.setLayoutManager(layoutManager)
                viewActivities.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) { }
        })
    }

    class LatestActivity(val act: Activities) : Item<GroupieViewHolder>(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.txtActivity.text = act.activity
        }
        override fun getLayout(): Int {
            return R.layout.activities_row
        }
    }

}