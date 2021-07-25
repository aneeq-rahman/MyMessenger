package com.aneeq.messenger.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.aneeq.messenger.R
import com.aneeq.messenger.activity.ChatLogActivity
import com.aneeq.messenger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class NewMessageAdapter(val context: Context, val userList: ArrayList<User>) :
    RecyclerView.Adapter<NewMessageAdapter.NewMessageViewHolder>() {
    class NewMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
        val img_newchat: CircleImageView = view.findViewById(R.id.img_newchat)
        val txtNewName: TextView = view.findViewById(R.id.txtNewName)
        val txtNewStatus: TextView = view.findViewById(R.id.txtNewStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewMessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_newchat_single_row, parent, false)
        return NewMessageViewHolder(
            view
        )

    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: NewMessageViewHolder, position: Int) {

        val users = userList[position]
        if(users.userId.equals(FirebaseAuth.getInstance().uid)){
            holder.itemView.visibility=View.GONE
            holder.itemView.layoutParams=RecyclerView.LayoutParams(0,0)
        }
else {
            holder.itemView.visibility = View.VISIBLE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        Picasso.get().load(users.profileUri).into(holder.img_newchat)
        holder.txtNewName.text = users.username
//holder.txtNewStatus.text=users.userStatus
        holder.llContent.setOnClickListener {
            val i= Intent(context, ChatLogActivity::class.java)
            i.putExtra("userdetails",users)
            context.startActivity(i)

        }


    }


}