package com.aneeq.messenger.activity

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aneeq.messenger.R
import com.aneeq.messenger.adapter.ChatFromAdapter
import com.aneeq.messenger.adapter.ChatToAdapter
import com.aneeq.messenger.adapter.ImageReceiveAdapter
import com.aneeq.messenger.adapter.ImageSendAdapter
import com.aneeq.messenger.model.ChatMessage
import com.aneeq.messenger.model.User
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatLogActivity : AppCompatActivity() {


    lateinit var recycle_from_to: RecyclerView
    lateinit var toolbar: Toolbar
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var frame: FrameLayout
    lateinit var collapsingtoolbar: CollapsingToolbarLayout
    lateinit var drawerLayout: DrawerLayout
    lateinit var etEnterMessage: EditText
    lateinit var btnSend: Button
    lateinit var attach: TextView
    var get: User? = null
    var window: PopupWindow? = null
    lateinit var layoutManager: RecyclerView.LayoutManager
    val adapter = GroupAdapter<GroupieViewHolder>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        get = intent.getParcelableExtra<User>("userdetails")
        setContentView(R.layout.activity_chat_log)
        recycle_from_to = findViewById(R.id.recycle_from_to)
        recycle_from_to.adapter = adapter
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        attach = findViewById(R.id.attach)
        frame = findViewById(R.id.frame)
        drawerLayout = findViewById(R.id.drawerLayout)
        layoutManager = LinearLayoutManager(this@ChatLogActivity)
        etEnterMessage = findViewById(R.id.etEnterMessage)
        btnSend = findViewById(R.id.btnSend)

        btnSend.setOnClickListener {
            etEnterMessage.text.toString().isEmpty() ?: return@setOnClickListener
            sendMessage()
        }
        setUptoolbar()
        toolbar.setOnClickListener {
            val i = Intent(this, OthersProfileActivity::class.java)
            i.putExtra("passid", get?.userId)
            startActivity(i)
        }
        listenMessages()
        attach.setOnClickListener {

            showPopup()


        }
    }

    private fun setUptoolbar() {

        setSupportActionBar(toolbar)
        supportActionBar?.title = get?.username
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivity(Intent(this, LatestMessengerActivity::class.java))
        finishAffinity()
        return super.onOptionsItemSelected(item)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendMessage() {
        val fromId = FirebaseAuth.getInstance().uid!!
        val toId = get!!.userId.toString()
        val sender = get!!.username.toString()
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val reverseref =
            FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        val timeStamp = LocalDateTime.now()
        val date = timeStamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val msg = etEnterMessage.text.toString()
        val chatMessage =
            ChatMessage(
                ref.key.toString(), fromId, sender,
                msg!!,"", toId,
                date
            )
        ref.setValue(chatMessage).addOnSuccessListener {
            etEnterMessage.text.clear()
            recycle_from_to.scrollToPosition(adapter.itemCount - 1)
        }
        reverseref.setValue(chatMessage)

        val latestref =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestref.setValue(chatMessage)
        val latestreverseref =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestreverseref.setValue(chatMessage)


    }

    private fun listenMessages() {
        val fromId = FirebaseAuth.getInstance().uid!!
        val toId = get!!.userId.toString()
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                //
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                //
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                //
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chat = p0.getValue(ChatMessage::class.java)
                if (chat != null ) {
                    if(chat.image!="") {
                        if (chat.fromId == FirebaseAuth.getInstance().uid)
                        {adapter.add(ImageSendAdapter(chat.image, LatestMessengerActivity.currentUser!!))}
                        else {adapter.add(ImageReceiveAdapter(chat.image, get!!))
                        Log.d("CLA","executed this loop")}
                    }
                    else if(chat.text!="")
                        if (chat.fromId == FirebaseAuth.getInstance().uid)
                        {adapter.add(ChatToAdapter(chat.text, LatestMessengerActivity.currentUser!!))}
                    else {
                        adapter.add(ChatFromAdapter(chat.text, get!!))
                    }
                }
                recycle_from_to.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                //
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun showPopup() {
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = inflater.inflate(R.layout.custom_popup, null)
        window = PopupWindow(
            view,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window!!.elevation = 9.0F
        }

        val imgAttach = view.findViewById<ImageButton>(R.id.imgAttach)
        val vidAttach = view.findViewById<ImageButton>(R.id.vidAttach)
        val docAttach = view.findViewById<ImageButton>(R.id.docAttach)

        imgAttach.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
            window!!.dismiss()
        }
        vidAttach.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "video/*"
            startActivityForResult(intent, 0)
            window!!.dismiss()
        }
        TransitionManager.beginDelayedTransition(frame)
        window!!.showAtLocation(frame, Gravity.BOTTOM, 0, 230)


    }


    var selectedPhoto: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhoto = data.data
            if (selectedPhoto.toString().contains("image")) {
                val image = Intent(this, ImageEditorActivity::class.java)
                image.putExtra("sendImage", selectedPhoto.toString())
                image.putExtra("sendto", get!!.userId)
                startActivity(image)
            } else if (selectedPhoto.toString().contains("video")) {
                val video = Intent(this, ImageEditorActivity::class.java)
                video.putExtra("sendVideo", selectedPhoto.toString())
                startActivity(video)
            }
        }
    }


}











