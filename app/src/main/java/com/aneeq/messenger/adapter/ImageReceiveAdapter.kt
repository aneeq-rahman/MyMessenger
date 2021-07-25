package com.aneeq.messenger.adapter

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import android.widget.Toast
import com.aneeq.messenger.R
import com.aneeq.messenger.activity.ChatLogActivity
import com.aneeq.messenger.model.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.recycler_recieveimage_single_row.view.*
import kotlinx.android.synthetic.main.recycler_sendimage_single_row.view.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception


class ImageReceiveAdapter(val image: String, val user: User) : Item<GroupieViewHolder>() {
    val TAG="IRA"
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        Picasso.get().load(user.profileUri).into(viewHolder.itemView.img_from)
        Picasso.get().load(image).into(viewHolder.itemView.imageRecieved)
       viewHolder.itemView.btnDownload.setOnClickListener {
            writeExternalStorage()
            viewHolder.itemView.btnDownload.visibility=View.GONE
        }
    }

    override fun getLayout(): Int {
        return R.layout.recycler_recieveimage_single_row
    }
    private fun writeExternalStorage(){
        val state=Environment.getExternalStorageState()
        if(Environment.MEDIA_MOUNTED == state){
            val root=Environment.getRootDirectory()
            val directory= File(root.absolutePath+"/KotlinMessenger/images")
            Log.d(TAG,"directory:$directory")
            directory.mkdir()
            val name= "$image.jpeg"
            val file=File(directory,name)
            try{

                val stream=FileOutputStream(file)

               //val bitmap=((BitmapDrawable)imageRecieved.getDrawable()).getBitmap()
                 // bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
                        stream.flush()
                        stream.close()
                        Log.d(TAG,"Image saved Successfully $stream" )




        }catch (e:Exception){
            e.printStackTrace()}
        }
        else{
            Log.d(TAG,"Try Again" )
        }

    }


}