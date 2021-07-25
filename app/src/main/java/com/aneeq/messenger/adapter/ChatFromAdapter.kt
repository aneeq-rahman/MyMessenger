package com.aneeq.messenger.adapter

import com.aneeq.messenger.R
import com.aneeq.messenger.model.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.recycler_chatfrom_single_row.view.*
import kotlinx.android.synthetic.main.recycler_chatto_single_row.view.*



class ChatFromAdapter(val text: String, val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.txt_from.text = text
        Picasso.get().load(user.profileUri).into(viewHolder.itemView.img_from)
    }

    override fun getLayout(): Int {
        return R.layout.recycler_chatfrom_single_row
    }
}


