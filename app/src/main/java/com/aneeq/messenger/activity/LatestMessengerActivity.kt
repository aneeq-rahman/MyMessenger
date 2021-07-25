package com.aneeq.messenger.activity

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.aneeq.messenger.util.ConnectionManager
import com.aneeq.messenger.R
import com.aneeq.messenger.R.*
import com.aneeq.messenger.adapter.LatestAdapter
import com.aneeq.messenger.model.ChatMessage
import com.aneeq.messenger.model.User
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

lateinit var toolbar: Toolbar
lateinit var sharedPreferences: SharedPreferences
lateinit var coordinatorLayout: CoordinatorLayout
lateinit var rlEmpty: RelativeLayout
lateinit var txtEmpty: TextView
lateinit var frame: FrameLayout
lateinit var collapsingtoolbar: CollapsingToolbarLayout
lateinit var drawerLayout: DrawerLayout
lateinit var recycle_Latest: RecyclerView
lateinit var notificationManager: NotificationManager
lateinit var notificationChannel: NotificationChannel
lateinit var builder: Notification.Builder
val channelId = "com.aneeq.messenger"
val description = "Test Notification"
val adapter = GroupAdapter<GroupieViewHolder>()
var status: Boolean = true

class LatestMessengerActivity : AppCompatActivity() {

    companion object {
        var currentUser: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // status=true
        sharedPreferences = getSharedPreferences("Messenger Preferences", Context.MODE_PRIVATE)
        status = sharedPreferences.getBoolean("online", false)
        setContentView(layout.activity_latest_messenger)
        recycle_Latest = findViewById(R.id.recycle_Latest)
        recycle_Latest.adapter = adapter

        recycle_Latest.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        collapsingtoolbar = findViewById(R.id.collapsingtoolbar)
        frame = findViewById(R.id.frame)
        drawerLayout = findViewById(R.id.drawerLayout)
        rlEmpty = findViewById(R.id.rlEmpty)
        txtEmpty = findViewById(R.id.txtEmpty)
        rlEmpty.visibility = View.GONE

        setUptoolbar()

        if (ConnectionManager().checkConnection(this)) {
            listenForLatest()
            fetchCurrentUser()
        } else {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Failure")
            dialog.setMessage("Internet Connection NOT Found")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }



        adapter.setOnItemClickListener { item, view ->
            val i = Intent(this, ChatLogActivity::class.java)
            val row = item as LatestAdapter
            i.putExtra("userdetails", row.user)
            startActivity(i)
        }

        if (FirebaseAuth.getInstance().uid == null) {
            val i = Intent(this, SignInActivity::class.java)
            startActivity(i)
            finishAffinity()
        }
        Log.d("latest", "Hello World")
    }

    override fun onResume() {
        sharedPreferences.edit().putBoolean("online", true).apply()
        super.onResume()
    }

    val latestMap = HashMap<String, ChatMessage>() //
    private fun refreshRecycler() {
        adapter.clear()
        latestMap.values.forEach {
            adapter.add(LatestAdapter(it))
        }

    }


    private fun sendNoteMessage(seename: String, seetext: String) {

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(applicationContext, LatestMessengerActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this@LatestMessengerActivity,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this@LatestMessengerActivity, channelId)
                .setContentTitle("Messenger")
                .setContentText("$seename:$seetext")
                .setSmallIcon(R.drawable.appicob)
                .setContentIntent(pendingIntent)
        } else {
            builder = Notification.Builder(this@LatestMessengerActivity)
                .setContentTitle("Messenger")
                .setContentText("$seename:$seetext")
                .setSmallIcon(R.drawable.appicob)
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(0, builder.build())

    }

    private fun listenForLatest() {

        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                //
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val latestMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMap[p0.key!!] = latestMessage //
                refreshRecycler()


                ////////////////////////////

                if (fromId == latestMessage.toId) {
                    val chatPartnerid: String //
                    if (latestMessage.fromId == FirebaseAuth.getInstance().uid) {
                        chatPartnerid = latestMessage.toId
                    } else {
                        chatPartnerid = latestMessage.fromId
                    }

                    val ref123 =
                        FirebaseDatabase.getInstance().getReference("/users/$chatPartnerid")
                    ref123.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            //
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            val user = p0.getValue(User::class.java)
                            if (!status) {
                                sendNoteMessage(user?.username!!, latestMessage.text)
                            }
                        }
                    })
                    ///////////////////////////

                }
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val latestMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMap[p0.key!!] = latestMessage
                refreshRecycler()
////////////////////////////////
                if (fromId == latestMessage.toId) {
                    val chatPartnerid: String //
                    if (latestMessage.fromId == FirebaseAuth.getInstance().uid) {
                        chatPartnerid = latestMessage.toId
                    } else {
                        chatPartnerid = latestMessage.fromId
                    }

                    val ref123 =
                        FirebaseDatabase.getInstance().getReference("/users/$chatPartnerid")
                    ref123.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            //
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            val user = p0.getValue(User::class.java)
                            if (!status) {
                                sendNoteMessage(user?.username!!, latestMessage.text)
                            }
                        }

                    })
                    ///////////////////////////

                }
                ///////////////////////////
            }


        })


    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                //
            }

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mess_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.new_message -> {
                val i = Intent(this, NewMessageActivity::class.java)
                startActivity(i)
            }
            R.id.sign_out -> {
                FirebaseAuth.getInstance().signOut()
                sharedPreferences.edit().clear().apply()
                val i = Intent(this, SignInActivity::class.java)
                startActivity(i)
                finishAffinity()
            }
            R.id.profile -> {
                startActivity(Intent(this, YourProfileActivity::class.java))
            }
            R.id.status -> {
                startActivity(Intent(this, StatusActivity::class.java))
            }
            R.id.txtrecog -> {
                startActivity(Intent(this, MainActivity::class.java))
            }
            R.id.imgediting -> {
                startActivity(Intent(this, ImageEditorActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun setUptoolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Kotlin Messenger"

    }

    override fun onPause() {
        sharedPreferences.edit().putBoolean("status", false).apply()
        super.onPause()
    }

}
