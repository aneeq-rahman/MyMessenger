package com.aneeq.messenger.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.SyncStateContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aneeq.messenger.R
import com.aneeq.messenger.adapter.NewMessageAdapter
import com.aneeq.messenger.adapter.StatusAdapter
import com.aneeq.messenger.model.Status
import com.aneeq.messenger.model.User
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class StatusActivity : AppCompatActivity() {
    private lateinit var statusAdapter: StatusAdapter
    lateinit var toolbar: Toolbar
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var txtMyStatus: TextView
    lateinit var txtStatusTime: TextView
    lateinit var txtOthersStatus: TextView
    lateinit var btnAdd: ImageButton
    lateinit var frame: FrameLayout
    lateinit var collapsingtoolbar: CollapsingToolbarLayout
    lateinit var drawerLayout: DrawerLayout
    lateinit var imgMyStatus: CircleImageView
    lateinit var llContent: LinearLayout
    lateinit var recycle_Status: RecyclerView
    var statusList = arrayListOf<Status>()
    lateinit var layoutManager: RecyclerView.LayoutManager
    var selectedMedia: Uri? = null

    companion object {
        const val IMAGE_PICK = 1000

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        llContent = findViewById(R.id.llContent)
        toolbar = findViewById(R.id.toolbar)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        txtMyStatus = findViewById(R.id.txtMyStatus)
        txtStatusTime = findViewById(R.id.txtStatusTime)
        txtOthersStatus = findViewById(R.id.txtOthersStatus)
        btnAdd = findViewById(R.id.btnAdd)
        frame = findViewById(R.id.frame)
        collapsingtoolbar = findViewById(R.id.collapsingtoolbar)
        drawerLayout = findViewById(R.id.drawerLayout)
        imgMyStatus = findViewById(R.id.imgMyStatus)
        Picasso.get().load(LatestMessengerActivity.currentUser!!.profileUri)
            .error(R.drawable.appicob).into(imgMyStatus)
        layoutManager = LinearLayoutManager(this)
        setUptoolbar()
        myStatus()
        fetchOthers()
        llContent.setOnClickListener {
            pickMedia()
        }
        if (intent.getBooleanExtra("delete", false)) {
            deletedStatus()
        }

    }

    private fun pickMedia() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/* video/*"
        startActivityForResult(intent, IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            selectedMedia = data.data
            if (selectedMedia == null) return
            else {
                val i = Intent(this, StatusConfirmActivity::class.java)
                i.putExtra("media", selectedMedia.toString())
                startActivity(i)

            }

        }
    }

    private fun setUptoolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Status"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

    }


    private fun myStatus() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-status/$uid")
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

            ///////////////////////////////////////////////////////////////////////////////////////////////////
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                btnAdd.visibility = View.GONE
                txtMyStatus.text = "My Status"

                ref.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        //
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val status = p0.getValue(Status::class.java) ?: return
                        txtStatusTime.text = status.time
                        Picasso.get().load(status.statusUri).error(R.drawable.appicob)
                            .into(imgMyStatus)
                    }

                })
                llContent.setOnClickListener {
                    val i = Intent(this@StatusActivity, StatusDisplayActivity::class.java)
                    startActivity(i)
                }
            }

            ////////////////////////////////////////////////////////////////////////////////////////////////
            override fun onChildRemoved(p0: DataSnapshot) {
                deletedStatus()
            }

        })


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        super.onBackPressed()
        finish()



        return super.onOptionsItemSelected(item)
    }

    private fun fetchOthers() {


        recycle_Status = findViewById(R.id.recycle_Status)
        recycle_Status.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        val ref = FirebaseDatabase.getInstance().getReference("/user-status")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                //
            }

            override fun onDataChange(p0: DataSnapshot) {

                p0.children.forEach {
                    statusList.add(it.getValue(Status::class.java)!!)
                }
                if (this != null) {
                    statusAdapter = StatusAdapter(
                        this@StatusActivity,
                        statusList
                    )
                    val mLayoutManager =
                        LinearLayoutManager(this@StatusActivity)
                    recycle_Status.layoutManager = mLayoutManager
                    recycle_Status.itemAnimator = DefaultItemAnimator()
                    recycle_Status.adapter = statusAdapter
                    recycle_Status.setHasFixedSize(true)
                }

            }

        })
    }

    private fun deletedStatus() {
        btnAdd.visibility = View.VISIBLE
        txtMyStatus.text = "My Status"
        txtStatusTime.text = "Tap to Add Status"
        Picasso.get().load(LatestMessengerActivity.currentUser!!.profileUri)
            .error(R.drawable.appicob).into(imgMyStatus)
    }
}