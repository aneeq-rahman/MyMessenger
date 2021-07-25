package com.aneeq.messenger.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.drawerlayout.widget.DrawerLayout
import com.aneeq.messenger.R
import com.aneeq.messenger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class YourProfileActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var frame: FrameLayout
    lateinit var drawerLayout: DrawerLayout
    lateinit var cicProfileLo: CircleImageView
    lateinit var btnProfile: ImageButton
    lateinit var txtName: TextView
    lateinit var txtemail: TextView
    lateinit var txtStatus: TextView
lateinit var btnUpdate:Button
    lateinit var etName:EditText
    lateinit var etStatus:EditText
    var selectedPhoto: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_profile)

txtStatus=findViewById(R.id.txtStatus)
        cicProfileLo = findViewById(R.id.cicProfileLo)
        btnProfile = findViewById(R.id.btnProfile)
        btnUpdate= findViewById(R.id.btnUpdate)
        txtName = findViewById(R.id.txtName)
        txtemail = findViewById(R.id.txtEmail)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frame = findViewById(R.id.frame)
        drawerLayout = findViewById(R.id.drawerLayout)

        setUpToolbar()
        setUpYourProfile()


        txtName.setOnClickListener {
            updateName()
        }
        txtStatus.setOnClickListener {
            updateStatus()
        }

        btnProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)

        }

        btnUpdate.setOnClickListener {
            if (selectedPhoto == null) {
                update()
            }
            else{
                updatePhoto()
            }
        }

    }
    private fun updateName(){
        val dialog = AlertDialog.Builder(this)
        etName=EditText(this)
        //etName.text=txtName.text as Editable?
        dialog.setTitle("Update Your Profile")
        dialog.setView(etName)
        dialog.setMessage("Enter Your Name")
        dialog.setPositiveButton("Okay") { _, _ ->
            txtName.text=etName.text
        }
        dialog.setNegativeButton("Cancel") { _, _ ->
            dialog.setView(null)
            dialog.show().cancel()

        }
       dialog.create()
        dialog.show()
    }

    private fun updateStatus(){
        val dialog = AlertDialog.Builder(this)
        etStatus=EditText(this)
       // etStatus.text= txtStatus.text as Editable?
        dialog.setTitle("Update Your Profile")
        dialog.setView(etStatus)
        dialog.setMessage("Enter Your Status")
        dialog.setPositiveButton("Okay") { _, _ ->
            txtStatus.text=etStatus.text
        }

        dialog.setNegativeButton("Cancel") { _, _ ->
            dialog.setView(null)
            dialog.show().cancel()

        }
        dialog.create()
        dialog.show()
    }

    private fun setUpYourProfile(){
        val ref=FirebaseDatabase.getInstance().getReference("/users/${FirebaseAuth.getInstance().uid}")
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //
            }

            override fun onDataChange(p0: DataSnapshot) {
                val p=p0.getValue(User::class.java)
                Picasso.get().load(p!!.profileUri).into(cicProfileLo)
                txtName.text=p.username
                txtemail.text=p.userEmail
            }

        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhoto = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhoto)
            cicProfileLo.setImageBitmap(bitmap)
            btnProfile.alpha = 0f
        }
    }


    private fun update() {
        val ref123 =
            FirebaseDatabase.getInstance().getReference("/users/${FirebaseAuth.getInstance().uid}")
        ref123.child("username").setValue(txtName.text.toString())
        ref123.child("userStatus").setValue(txtStatus.text.toString())
            .addOnSuccessListener {
                Toast.makeText(this, "Update Successful", Toast.LENGTH_LONG).show()
            }.addOnFailureListener {}
    }
private fun updatePhoto(){
    val ref123 =
        FirebaseDatabase.getInstance().getReference("/users/${FirebaseAuth.getInstance().uid}")
    ref123.child("username").setValue(txtName.text.toString())
    ref123.child("userStatus").setValue(txtStatus.text.toString())
        .addOnSuccessListener {
            Toast.makeText(this, "Update Successful", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {}


        val ref = FirebaseStorage.getInstance().getReference("/images/${txtName.text.toString()}")
        ref.delete()
        if (selectedPhoto == null) return
        ref.putFile(selectedPhoto!!).addOnSuccessListener{
            ref.downloadUrl.addOnSuccessListener {
            ref123.child("profileUri").setValue(it.toString())
                .addOnSuccessListener {
                    Toast.makeText(this, "Update Successful", Toast.LENGTH_LONG).show()
                }.addOnFailureListener{}
            }.addOnFailureListener{}
        }.addOnFailureListener{}
    }


    private fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="Your Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }





}
