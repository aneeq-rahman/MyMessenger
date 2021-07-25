package com.aneeq.messenger.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.aneeq.messenger.R
import com.aneeq.messenger.activity.LatestMessengerActivity
import com.aneeq.messenger.activity.StatusDisplayActivity
import com.aneeq.messenger.model.ViewBy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ViewedByAdapter(val context: Context, val viewList: ArrayList<ViewBy>) :
    RecyclerView.Adapter<ViewedByAdapter.ViewByViewHolder>() {
    class ViewByViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
        val imgMyStatus: CircleImageView = view.findViewById(R.id.imgMyStatus)
        val txtMyStatus: TextView = view.findViewById(R.id.txtMyStatus)
        val txtStatusTime: TextView = view.findViewById(R.id.txtStatusTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewByViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_status_single_row, parent, false)
        return ViewByViewHolder(
            view
        )

    }

    override fun getItemCount(): Int {
        return viewList.size
    }
    override fun onBindViewHolder(holder: ViewByViewHolder, position: Int) {

        val viewBy = viewList[position]
        holder.itemView.layoutParams = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            50
        )
        Picasso.get().load(viewBy.mypic).into(holder.imgMyStatus)
        holder.txtMyStatus.text = viewBy.myname
        holder.txtStatusTime.text = viewBy.myTime
    }


}