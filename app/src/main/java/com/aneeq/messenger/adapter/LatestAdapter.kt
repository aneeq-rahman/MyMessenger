package com.aneeq.messenger.adapter

import android.content.Intent
import com.aneeq.messenger.R
import com.aneeq.messenger.activity.LatestMessengerActivity
import com.aneeq.messenger.model.ChatMessage
import com.aneeq.messenger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.recycler_latestmessage_single_row.view.*

class LatestAdapter(val latest: ChatMessage): Item<GroupieViewHolder>(){

    var user:User?=null
    var text:String?=null
    override fun getLayout(): Int {
        return R.layout.recycler_latestmessage_single_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        if(latest.image!=""){
            text="Image"
        }
        else if(latest.text!="" && latest.text.length>7) {
            text = latest.text.substring(0, 8) + "..."
        }else{text=latest.text}
        viewHolder.itemView.txtLatestMessage.text = text

        val chatPartnerid:String //
        if(latest.fromId== FirebaseAuth.getInstance().uid){
            chatPartnerid=latest.toId
        }
        else{
            chatPartnerid=latest.fromId
        }

        val ref= FirebaseDatabase.getInstance().getReference("/users/$chatPartnerid")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                //
            }

            override fun onDataChange(p0: DataSnapshot) {
                user=p0.getValue(User::class.java) ?:return
                viewHolder.itemView.txtLatestName.text=user?.username


                Picasso.get().load(user!!.profileUri).error(R.drawable.appicob).into(viewHolder.itemView.imgLatest)
                viewHolder.itemView.txtLatestDate.text=latest.datetime
            }

        })



    }
private fun userName():String{
    return user?.username!!
}

}