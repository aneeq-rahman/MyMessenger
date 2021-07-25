package com.aneeq.messenger.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.*
import androidx.core.app.ActivityCompat
import com.aneeq.messenger.R.*
import com.aneeq.messenger.model.User
import com.aneeq.messenger.util.ConnectionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class SignUpActivity : AppCompatActivity() {

    lateinit var btnProfile: Button
    lateinit var etName: EditText
    lateinit var etPass: EditText
    lateinit var etEmail: EditText
    lateinit var btnSignUp: Button

    lateinit var cicProfile: CircleImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_sign_up)

        btnProfile = findViewById(id.btnProfile)
        cicProfile = findViewById(id.cicProfile)
        etName = findViewById(id.etName)
        etPass = findViewById(id.etPass)
        etEmail = findViewById(id.etEmail)
        btnSignUp = findViewById(id.btnSignUp)



        btnSignUp.setOnClickListener {
            if (ConnectionManager().checkConnection(this)) {
                setUpSignUp()
            }
            else{
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
        }

        btnProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhoto: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) 0{
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhoto = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhoto)
            cicProfile.setImageBitmap(bitmap)
            btnProfile.alpha = 0f
        }
    }


    private fun setUpSignUp() {
        val emailString = etEmail.text.toString()
        val passString = etPass.text.toString()

        if(emailString==null && passString==null) return
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(emailString, passString)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                Toast.makeText(
                    this@SignUpActivity,
                    "SignUp successful,your Id: ${it.result?.user?.uid}",
                    Toast.LENGTH_LONG
                ).show()
                uploadImgToFirebaseStorage()

            }
            .addOnFailureListener {
                Toast.makeText(
                    this@SignUpActivity,
                    "failed to created user because ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

    }



    private fun uploadImgToFirebaseStorage() {
        if (selectedPhoto == null) return

        val ref = FirebaseStorage.getInstance().getReference("/images/${etName.text.toString()}")
        ref.putFile(selectedPhoto!!).addOnSuccessListener {

            ///////////////////////////////////////
            ref.downloadUrl.addOnSuccessListener {
                val uId = FirebaseAuth.getInstance().uid ?: ""
                val reff = FirebaseDatabase.getInstance().getReference("/users/$uId")
                val user = User(
                    uId,
                    etName.text.toString(),
                    it.toString(),
                    etEmail.text.toString(),"Hey!I am using KFM"
                )
                reff.setValue(user).addOnSuccessListener {
                    val intent=Intent(this,
                        SignInActivity::class.java)
                    startActivity(intent)
                    finishAffinity()

                }
                    .addOnFailureListener {
                        Toast.makeText(
                            this@SignUpActivity,
                            " database ${it.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
                .addOnFailureListener { //
                }
        }


            .addOnFailureListener {
                Toast.makeText(
                    this@SignUpActivity,
                    "failed because ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

    }
}


