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
import com.aneeq.messenger.model.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class StatusAdapter(val context: Context, val statusList: ArrayList<Status>) :
    RecyclerView.Adapter<StatusAdapter.StatusViewHolder>() {
    class StatusViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
        val imgMyStatus: CircleImageView = view.findViewById(R.id.imgMyStatus)
        val txtMyStatus: TextView = view.findViewById(R.id.txtMyStatus)
        val txtStatusTime: TextView = view.findViewById(R.id.txtStatusTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_status_single_row, parent, false)
        return StatusViewHolder(
            view
        )

    }

    override fun getItemCount(): Int {
        return statusList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {

        val status = statusList[position]
        if (status.id.equals(FirebaseAuth.getInstance().uid)) {
            holder.itemView.visibility = android.view.View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        } else {
            holder.itemView.visibility = android.view.View.VISIBLE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        Picasso.get().load(status.statusUri).into(holder.imgMyStatus)
        holder.txtMyStatus.text = status.name
        holder.txtStatusTime.text = status.time
        holder.llContent.setOnClickListener {
            val uid = FirebaseAuth.getInstance().uid
            val ref = FirebaseDatabase.getInstance()
                .getReference("/user-status/${status.id}/Viewed By/$uid")


            val mytime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"))
            val view = com.aneeq.messenger.model.ViewBy(
                LatestMessengerActivity.currentUser!!.username!!,
                LatestMessengerActivity.currentUser!!.profileUri!!,
                mytime
            )
            ref.setValue(view)
                .addOnSuccessListener {
                    val i = Intent(context, StatusDisplayActivity::class.java)
                    i.putExtra("others", true)
                    i.putExtra("id", status.id)
                    context.startActivity(i)
                }.addOnFailureListener { }


        }


    }


}