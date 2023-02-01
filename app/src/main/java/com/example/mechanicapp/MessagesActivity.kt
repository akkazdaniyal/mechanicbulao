package com.example.mechanicapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_messages.*
import kotlinx.android.synthetic.main.activity_messages.view.*
import kotlinx.android.synthetic.main.messages_row.view.*
import kotlin.math.log

class MessagesActivity : AppCompatActivity() {

    companion object{
        var currentUser: Users? = null
        var mechanic: Mechanic? = null
        val MECHANIC_KEY = "MECHANIC_KEY"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        viewMessages.adapter = adapter

        adapter.setOnItemClickListener{item, view ->
            val intent = Intent(this, ChatlogActivity::class.java)
            val row = item as LatestMessage
            intent.putExtra(MECHANIC_KEY, row.mechanic)
            startActivity(intent)
        }

        listenforLatestMessages()
        fetchCurrentUser()

        val backmessages = findViewById<View>(R.id.backMessages) as ImageButton
        backmessages.setOnClickListener{
            val intent = Intent(this,DashboardActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }
    }

    val latestMessageMap = HashMap<String,ChatMessage>()

    private fun refreshViewMessages(){
        adapter.clear()
        latestMessageMap.values.forEach{
            adapter.add(LatestMessage(it))
        }
    }

    private fun listenforLatestMessages(){
        val senderId = FirebaseAuth.getInstance().uid
        val recieverId = mechanic?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-message/$senderId")
        ref.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessageMap[snapshot.key!!] = chatMessage
                refreshViewMessages()
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessageMap[snapshot.key!!] = chatMessage
                refreshViewMessages()
            }
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
        })
    }
    val adapter = GroupieAdapter()

    private fun fetchCurrentUser(){
        val uid= FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("Users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(Users::class.java)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}