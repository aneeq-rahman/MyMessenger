package com.aneeq.messenger.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aneeq.messenger.adapter.NewMessageAdapter
import com.aneeq.messenger.R
import com.aneeq.messenger.R.*
import com.aneeq.messenger.model.User
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NewMessageActivity : AppCompatActivity() {
    private lateinit var newMessageAdapter: NewMessageAdapter
    lateinit var recycle_chats:RecyclerView
    lateinit var toolbar: Toolbar
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var frame: FrameLayout
    lateinit var collapsingtoolbar: CollapsingToolbarLayout
    lateinit var drawerLayout: DrawerLayout
    var usersList=arrayListOf<User>()
    lateinit var layoutManager: RecyclerView.LayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_new_message)


        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        collapsingtoolbar = findViewById(R.id.collapsingtoolbar)
        frame = findViewById(R.id.frame)
        drawerLayout = findViewById(R.id.drawerLayout)
        layoutManager = LinearLayoutManager(this@NewMessageActivity)

        setUptoolbar()
        fetchUsers()
    }
    private fun fetchUsers(){


        recycle_chats=findViewById(R.id.recycle_chats)
        recycle_chats.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            ))
        val ref=FirebaseDatabase.getInstance().getReference("/users")
   ref.addListenerForSingleValueEvent(object :ValueEventListener{
       override fun onCancelled(p0: DatabaseError) {
           Log.d("new message",p0.toString())
       }

       override fun onDataChange(p0: DataSnapshot) {
           //val adapter=GroupAdapter<GroupieViewHolder>()

          p0.children.forEach {
             val user=it.getValue(User::class.java)
              usersList.add(it.getValue(User::class.java)!!)
              Log.d("new message","${user?.userId}")
              //if(user!=null) {
                  //adapter.add(UserItem(user))
              //}



          }



           if (this@NewMessageActivity != null) {
               newMessageAdapter = NewMessageAdapter(
                   this@NewMessageActivity,
                   usersList
               )
               val mLayoutManager =
                   LinearLayoutManager(this@NewMessageActivity)
               recycle_chats.layoutManager = mLayoutManager
               recycle_chats.itemAnimator = DefaultItemAnimator()
               recycle_chats.adapter = newMessageAdapter
               recycle_chats.setHasFixedSize(true)
           }

       }

   })
    }
    private fun setUptoolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="New Message"
supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}
