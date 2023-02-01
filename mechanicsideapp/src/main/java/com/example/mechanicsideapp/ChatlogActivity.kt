package com.example.mechanicsideapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chatlog.*
import kotlinx.android.synthetic.main.reciever_row_chatlog.view.*
import kotlinx.android.synthetic.main.sender_row_chatlog.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatlogActivity : AppCompatActivity() {
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    companion object{
        val TAG = "Chatlog"
    }

    val adapter = GroupieAdapter()
    var recieverUser : Users? = null
    var fullName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatlog)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.getReference("Mechanics")
        mAuth = FirebaseAuth.getInstance()

        val chatlogView = findViewById<View>(R.id.viewChatlog) as RecyclerView
        chatlogView.adapter = adapter

        val txtUsernameChatlog = findViewById<View>(R.id.txtUsernameChatlog) as TextView
        recieverUser = intent.getParcelableExtra<Users>(MessagesActivity.USER_KEY)
        txtUsernameChatlog.setText(recieverUser?.full_name)

        listenForMessages()

        val backChatlog = findViewById<View>(R.id.backChatlog) as ImageButton
        backChatlog.setOnClickListener{
            val intent = Intent(this,MessagesActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        val btnSend = findViewById<View>(R.id.btnSendMessage) as Button
        btnSend.setOnClickListener{
            performSendMessage()
            txtMessageChatlog.text.clear()
            chatlogView.scrollToPosition(adapter.itemCount - 1)

            val titleNotification = "Message from " + fullName
            val textMessage = txtMessageChatlog.text.toString()
            if(titleNotification.isNotEmpty() && textMessage.isNotEmpty()){
                PushNotification(
                    NotificationData(titleNotification,textMessage),
                    recieverUser!!.uid
                ).also {
                    sendNotification(it)
                }
            }
        }

        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                fullName = snapshot.child("full_name").value.toString()
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful){
                Log.d("sendMessageNotification","Response: ${Gson().toJson(response)}")
            }else{
                Log.e("sendMessageNotification", response.errorBody().toString())
            }
        }catch (e: Exception){
            Log.e("sendMessageNotification",e.toString())
        }
    }

    private fun listenForMessages(){
        val recieverId = recieverUser?.uid
        val senderId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-based-messages/$senderId/$recieverId")

        ref.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                val loggedinUser = MessagesActivity.currentUser ?: return

                if(chatMessage != null){
                    val uid = FirebaseAuth.getInstance().uid
                    if(chatMessage.recieverId == uid){
                        adapter.add(ChatRecieverItem(chatMessage.text,recieverUser!!))
                    } else{
                        adapter.add(ChatSenderItem(chatMessage.text,loggedinUser!!))
                    }
                }
                viewChatlog.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }
        })
    }

    private fun performSendMessage(){
        val text = txtMessageChatlog.text.toString()
        val senderId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<Users>(MessagesActivity.USER_KEY)
        val recieverId = user?.uid

//        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-based-messages/$recieverId/$senderId").push()
        val senderReference = FirebaseDatabase.getInstance().getReference("/user-based-messages/$senderId/$recieverId").push()

        if(senderId == null) return
        val chatMessage = ChatMessage(reference.key!!,text,senderId, recieverId!!,System.currentTimeMillis()/1000)
        reference.setValue(chatMessage)
        senderReference.setValue(chatMessage)

        val latestMessage = FirebaseDatabase.getInstance().getReference("/latest-message/$recieverId/$senderId")
        latestMessage.setValue(chatMessage)

        val latestSenderMessage = FirebaseDatabase.getInstance().getReference("/latest-message/$senderId/$recieverId")
        latestSenderMessage.setValue(chatMessage)

        val latestActivity = FirebaseDatabase.getInstance().getReference("/latest-activities/$recieverId").push()
        val activities = Activities(latestActivity.key!!,"You have recieved a new message from $fullName",recieverId,System.currentTimeMillis()/1000)
        latestActivity.setValue(activities)
    }
}

class ChatSenderItem(val text: String,val mechanic: Mechanic?): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.txtSenderMessageChatlog.text = text

        val uri = mechanic?.profileImageUrl
        val targetImageview = viewHolder.itemView.imgViewSender

        if(uri!!.isEmpty()){
            Picasso.get().load(R.drawable.ic_user).into(targetImageview)
        }else{
            Picasso.get().load(uri).into(targetImageview)
        }
    }

    override fun getLayout(): Int {
        return R.layout.sender_row_chatlog
    }
}

class ChatRecieverItem(val text: String, val user: Users): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.txtRecieverMessageChatlog.text = text

        val uri = user.profileImageUrl
        val targetImageview = viewHolder.itemView.imgViewReciever

        if(uri.isEmpty()){
            Picasso.get().load(R.drawable.ic_user).into(targetImageview)
        }else{
            Picasso.get().load(uri).into(targetImageview)
        }
    }

    override fun getLayout(): Int {
        return R.layout.reciever_row_chatlog
    }
}