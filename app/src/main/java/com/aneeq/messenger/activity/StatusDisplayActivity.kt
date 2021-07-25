package com.aneeq.messenger.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aneeq.messenger.R
import com.aneeq.messenger.adapter.StatusAdapter
import com.aneeq.messenger.adapter.ViewedByAdapter
import com.aneeq.messenger.model.Status
import com.aneeq.messenger.model.ViewBy
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class StatusDisplayActivity : AppCompatActivity() {
    private lateinit var viewedByAdapter: ViewedByAdapter
    var viewList=arrayListOf<ViewBy>()
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var recycle_views: RecyclerView
    lateinit var toolbar: Toolbar
    lateinit var imgStatus: ImageView
    lateinit var vidStatus: VideoView
    lateinit var txtCaption: TextView
    lateinit var statusBar: ProgressBar
    lateinit var txtViewed:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_display)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        recycle_views = findViewById(R.id.recycle_views)
        val mBottomSheetBehavior=BottomSheetBehavior.from(recycle_views)

        toolbar = findViewById(R.id.toolbar)
        imgStatus = findViewById(R.id.imgStatus)
        vidStatus = findViewById(R.id.vidStatus)
        txtCaption = findViewById(R.id.txtCaption)
        txtViewed = findViewById(R.id.txtViewed)
        statusBar = findViewById(R.id.statusBar)
        layoutManager = LinearLayoutManager(this)
        fetchViewers()
        setUpToolbar()
        if (intent.getBooleanExtra("others", false)) {
            setUpOthersDisplay()
            txtViewed.visibility=View.GONE
        } else {
            setUpMyDisplay()
            txtViewed.text=" " + "${viewList.size}"
            txtViewed.visibility=View.VISIBLE
            txtViewed.setOnClickListener {
                if(mBottomSheetBehavior.state!=BottomSheetBehavior.STATE_EXPANDED){
                    mBottomSheetBehavior.state=BottomSheetBehavior.STATE_EXPANDED
                }
                else{
                    mBottomSheetBehavior.state=BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }
    }

    private fun setUpMyDisplay() {
        val prgAnim: ObjectAnimator = ObjectAnimator.ofInt(statusBar, "progress", 0, 100)

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-status/$uid")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                //
            }

            override fun onDataChange(p0: DataSnapshot) {
                val status = p0.getValue(Status::class.java)
               // val vidUri = Uri.parse(status!!.statusUri)
                if (status!!.statusUri.contains("image")) {
                    vidStatus.visibility = View.GONE
                    imgStatus.visibility = View.VISIBLE
                    Picasso.get().load(status.statusUri).error(R.drawable.appicob).into(imgStatus)
                    txtCaption.text = status.caption
                } else if (status.statusUri.contains("video")) {
                    vidStatus.visibility = View.VISIBLE
                    imgStatus.visibility = View.GONE
                  //  vidStatus.setVideoURI(vidUri)
                    vidStatus.requestFocus()
                    vidStatus.start()
                    txtCaption.text = status.caption
                }
            }

        })
        prgAnim.duration = 20000
        prgAnim.start()
        prgAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                val intent = Intent(this@StatusDisplayActivity, StatusActivity::class.java)
                intent.putExtra("over", 0)
                startActivity(intent)
                finishAffinity()
                statusBar.visibility = View.VISIBLE
            }
        })

    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Status"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

    }

    private fun setUpOthersDisplay() {
        val prgAnim: ObjectAnimator = ObjectAnimator.ofInt(statusBar, "progress", 0, 100)

        val uid = intent.getStringExtra("id")
        val ref = FirebaseDatabase.getInstance().getReference("/user-status/$uid")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                //
            }

            override fun onDataChange(p0: DataSnapshot) {
                val status = p0.getValue(Status::class.java)
                val vidUri = Uri.parse(status!!.statusUri)
                if (status.statusUri.contains("image")) {
                    vidStatus.visibility = View.GONE
                    imgStatus.visibility = View.VISIBLE
                    Picasso.get().load(status.statusUri).error(R.drawable.appicob).into(imgStatus)
                    txtCaption.text = status.caption
                } else if (status.statusUri.contains("video")) {
                    vidStatus.visibility = View.VISIBLE
                    imgStatus.visibility = View.GONE
                    vidStatus.setVideoURI(vidUri)
                    vidStatus.requestFocus()
                    vidStatus.start()
                    txtCaption.text = status.caption
                }
            }

        })
        prgAnim.duration = 20000
        prgAnim.start()
        prgAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                val intent = Intent(this@StatusDisplayActivity, StatusActivity::class.java)
                intent.putExtra("over", 0)
                startActivity(intent)
                finishAffinity()
                statusBar.visibility = View.VISIBLE
            }
        })

    }
    private fun fetchViewers(){


        recycle_views=findViewById(R.id.recycle_views)
        recycle_views.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        val ref=FirebaseDatabase.getInstance().getReference("/user-status/Viewed By")
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //
            }
            override fun onDataChange(p0: DataSnapshot) {

                p0.children.forEach {
                    viewList.add(it.getValue(ViewBy::class.java)!!)
                }
                if (this != null) {
                    viewedByAdapter = ViewedByAdapter(
                        this@StatusDisplayActivity,
                        viewList
                    )
                    val mLayoutManager =
                        LinearLayoutManager(this@StatusDisplayActivity)
                    recycle_views.layoutManager = mLayoutManager
                    recycle_views.itemAnimator = DefaultItemAnimator()
                    recycle_views.adapter = viewedByAdapter
                    recycle_views.setHasFixedSize(true)
                }

            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.status_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.delete -> {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Warning")
                dialog.setMessage("Delete Your Status?")
                dialog.setPositiveButton("Yes") { _, _ ->
                    val uid = FirebaseAuth.getInstance().uid
                    val ref = FirebaseDatabase.getInstance().getReference("/user-status/$uid")
                    ref.setValue(null).addOnSuccessListener {
                        Toast.makeText(this, "Status Deleted", Toast.LENGTH_LONG).show()
                        val i=Intent(this, StatusActivity::class.java)
                        i.putExtra("delete",true)
                        startActivity(i)
                        finish()
                    }
                }

                dialog.setNegativeButton("Cancel") { _, _ ->
                    dialog.show().cancel()

                }
                dialog.create()
                dialog.show()
            }

            else -> {
                super.onBackPressed()
                finish()
            }

        }
        return super.onOptionsItemSelected(item)
    }
}