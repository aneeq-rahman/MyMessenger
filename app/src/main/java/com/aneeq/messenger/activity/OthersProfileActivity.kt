package com.aneeq.messenger.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import com.aneeq.messenger.R
import com.aneeq.messenger.model.User
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class OthersProfileActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var collapsingtoolbar: CollapsingToolbarLayout
    lateinit var drawerLayout: DrawerLayout
    lateinit var frame:FrameLayout
     lateinit var imgOthers:ImageView
    lateinit var txtName: TextView
    lateinit var txtemail: TextView
lateinit var txtStatus:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_others_profile)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        collapsingtoolbar = findViewById(R.id.collapsingtoolbar)
        txtName = findViewById(R.id.txtName)
        txtemail = findViewById(R.id.txtEmail)
        frame = findViewById(R.id.frame)
        imgOthers = findViewById(R.id.imgOthers)
        txtStatus = findViewById(R.id.txtStatus)
        setUpToolbar()
        displayOthers()
    }

    private fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="Others Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }
private fun displayOthers(){
    val ref= FirebaseDatabase.getInstance().getReference("/users/${intent.getStringExtra("passid")}")
    ref.addValueEventListener(object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            //
        }

        override fun onDataChange(p0: DataSnapshot) {
            val p=p0.getValue(User::class.java)
            Picasso.get().load(p!!.profileUri).into(imgOthers)
            txtName.text=p.username
            txtemail.text=p.userEmail
            Log.d("others","${p.username}")
        }

    })
}
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}