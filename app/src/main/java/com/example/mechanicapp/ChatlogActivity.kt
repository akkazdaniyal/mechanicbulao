package com.example.mechanicapp

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
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessaging.getInstance
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chatlog.*
import kotlinx.android.synthetic.main.messages_row.view.*
import kotlinx.android.synthetic.main.reciever_row_chatlog.view.*
import kotlinx.android.synthetic.main.sender_row_chatlog.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val TOPIC = "/topics/myTopic"

class ChatlogActivity : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    companion object{
        val TAG = "Chatlog"
    }

    val adapter = GroupieAdapter()
    var recieverMechanic  : Mechanic? = null
    var fullName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatlog)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.getReference("Users")
        mAuth = FirebaseAuth.getInstance()

        val chatlogView = findViewById<View>(R.id.viewChatlog) as RecyclerView
        chatlogView.adapter = adapter

        val txtMechanicnameChatlog = findViewById<View>(R.id.txtMechanicnameChatlog) as TextView
        val mechMessage = intent.getParcelableExtra<Mechanic>(MessagesActivity.MECHANIC_KEY)
        if(mechMessage != null){
            recieverMechanic = intent.getParcelableExtra<Mechanic>(MessagesActivity.MECHANIC_KEY)
        }
        else{
            recieverMechanic = intent.getParcelableExtra<Mechanic>(MechanicprofileActivity.MECHANIC_KEY)
        }

        val fullNamen = recieverMechanic?.full_name
        if(fullNamen != ""){
            val arr = fullNamen?.split(" ")
            val fName = arr?.get(0)
            val lName = arr?.get(1)
            txtMechanicnameChatlog.text = fName
        }else{
            txtMechanicnameChatlog.text = "Happy User"
        }

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
            chatlogView.scrollToPosition(adapter.itemCount - 1)

            val titleNotification = "Message from " + fullName
            val textMessage = txtMessageChatlog.text.toString()
            Log.d("Token","T = $textMessage")
            Log.d("Token","T = $titleNotification")
            if(titleNotification.isNotEmpty() && textMessage.isNotEmpty()){
                PushNotification(
                    NotificationData(titleNotification,textMessage),
                    recieverMechanic!!.uid
                ).also {
                    sendNotification(it)
                }
            }
            txtMessageChatlog.text.clear()
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

    private fun listenForMessages(){
        val recieverId = recieverMechanic?.uid
        val senderId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-based-messages/$senderId/$recieverId")

        ref.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                val loggedinUser = MessagesActivity.currentUser ?: return

                if(chatMessage != null){
                    val uid = FirebaseAuth.getInstance().uid
                    if(chatMessage.recieverId == uid){
                        adapter.add(ChatRecieverItem(chatMessage.text,recieverMechanic!!))
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

    private fun performSendMessage(){
        val text = txtMessageChatlog.text.toString()
        val senderId = FirebaseAuth.getInstance().uid
        val mechanic = intent.getParcelableExtra<Mechanic>(NearbymechanicsActivity.MECHANIC_KEY)
        val recieverId = mechanic?.uid

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

class ChatRecieverItem(val text: String,val mechanic: Mechanic?): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.txtRecieverMessageChatlog.text = text

        val uri = mechanic?.profileImageUrl
        val targetImageview = viewHolder.itemView.imgViewReciever

        if(uri!!.isEmpty()){
            Picasso.get().load(R.drawable.ic_user).into(targetImageview)
        }else{
            Picasso.get().load(uri).into(targetImageview)
        }
    }

    override fun getLayout(): Int {
        return R.layout.reciever_row_chatlog
    }
}

class ChatSenderItem(val text: String, val user: Users): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.txtSenderMessageChatlog.text = text

        val uri = user.profileImageUrl
        val targetImageview = viewHolder.itemView.imgViewSender

        if(uri.isEmpty()){
            Picasso.get().load(R.drawable.ic_user).into(targetImageview)
        }else{
            Picasso.get().load(uri).into(targetImageview)
        }
    }

    override fun getLayout(): Int {
        return R.layout.sender_row_chatlog
    }
}