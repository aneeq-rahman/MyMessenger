package com.aneeq.messenger.adapter

import com.aneeq.messenger.R
import com.aneeq.messenger.model.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.recycler_chatto_single_row.view.*


class ChatToAdapter(val text: String, val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.txt_to.text = text
        Picasso.get().load(user.profileUri).into(viewHolder.itemView.img_to)
    }


    override fun getLayout(): Int {
        return R.layout.recycler_chatto_single_row
    }
}
