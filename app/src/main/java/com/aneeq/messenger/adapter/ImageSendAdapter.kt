package com.aneeq.messenger.adapter

import com.aneeq.messenger.R
import com.aneeq.messenger.model.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.recycler_sendimage_single_row.view.*

class ImageSendAdapter(val image: String, val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        Picasso.get().load(user.profileUri).into(viewHolder.itemView.img_to)
        Picasso.get().load(image).into(viewHolder.itemView.imageSent)

    }

    override fun getLayout(): Int {
        return R.layout.recycler_sendimage_single_row
    }
}