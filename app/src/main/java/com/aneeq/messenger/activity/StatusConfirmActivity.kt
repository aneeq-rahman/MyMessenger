package com.aneeq.messenger.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import com.aneeq.messenger.R
import com.aneeq.messenger.model.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class StatusConfirmActivity : AppCompatActivity() {
    lateinit var toolbar:Toolbar
    lateinit var imgStatusConfirm:ImageView
    lateinit var vidStatusConfirm:VideoView
    lateinit var etCaption:EditText
    lateinit var btnSend: ImageButton
    var confirm:Uri?=null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_confirm)
        confirm= Uri.parse(intent!!.getStringExtra("media"))
        toolbar = findViewById(R.id.toolbar)
        imgStatusConfirm=findViewById(R.id.imgStatusConfirm)
        vidStatusConfirm=findViewById(R.id.vidStatusConfirm)
        etCaption=findViewById(R.id.etCaption)
        btnSend=findViewById(R.id.btnSend)
        setUpToolbar()
        setUpConfirm()
        btnSend.setOnClickListener {
            sendStatus()
        }


    }
    private fun setUpConfirm(){
       // confirm= Uri.parse(intent.getStringExtra("media"))
        if(confirm.toString().contains("image")){
            vidStatusConfirm.visibility= View.GONE
            imgStatusConfirm.visibility= View.VISIBLE
            Picasso.get().load(confirm).into(imgStatusConfirm)
        }
        else if(confirm.toString().contains("video")){
            vidStatusConfirm.visibility= View.VISIBLE
            imgStatusConfirm.visibility= View.GONE
            vidStatusConfirm.setVideoURI(confirm)
            vidStatusConfirm.requestFocus()
            vidStatusConfirm.start()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendStatus() {


        val str = FirebaseStorage.getInstance()
            .getReference("/status/${LatestMessengerActivity.currentUser!!.username}")
        str.putFile(confirm!!).addOnSuccessListener {
            str.downloadUrl.addOnSuccessListener {
                val uid = FirebaseAuth.getInstance().uid
                val ref = FirebaseDatabase.getInstance().getReference("/user-status/$uid")
                val timeStamp = LocalDateTime.now()
                val date = timeStamp.format(DateTimeFormatter.ofPattern("hh:mm a"))
                val status = Status(uid!!,LatestMessengerActivity.currentUser!!.username,it.toString(), date,etCaption.text.toString())
                ref.setValue(status).addOnSuccessListener {
                    Toast.makeText(this,"Status Uploaded Successfully",Toast.LENGTH_SHORT).show()
                    val i = Intent(this, StatusDisplayActivity::class.java)
                    i.putExtra("caption", etCaption.text.toString())
                    i.putExtra("confirm", confirm.toString())
                    startActivity(i)
                    finish()
                }.addOnFailureListener {}
            }.addOnFailureListener {}
        }.addOnFailureListener {}
    }




    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Status"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onBackPressed()
        finish()
        return super.onOptionsItemSelected(item)
    }
}