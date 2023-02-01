package com.example.mechanicsideapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.messages_row.view.*

class LatestMessage(val chatMessage: ChatMessage) : Item<GroupieViewHolder>(){
    var user: Users? = null
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.txtUserLatestMessage.text = chatMessage.text

        val chatRecieverId: String
        if(chatMessage.senderId == FirebaseAuth.getInstance().uid){
            chatRecieverId = chatMessage.recieverId
        }else{
            chatRecieverId = chatMessage.senderId
        }

        val ref = FirebaseDatabase.getInstance().getReference("Users/$chatRecieverId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(Users::class.java)
                viewHolder.itemView.txtUsernameMessages.text = user?.full_name

                val targetImageview = viewHolder.itemView.imgViewUserMessages
                val imageLink = user?.profileImageUrl
                if(imageLink!!.isNotEmpty()){
                    Picasso.get().load(imageLink).into(targetImageview)
                }else{
                    Picasso.get().load(R.drawable.ic_user).into(targetImageview)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    override fun getLayout(): Int {
        return R.layout.messages_row
    }
}