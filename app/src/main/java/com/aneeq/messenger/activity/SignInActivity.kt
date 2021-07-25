package com.aneeq.messenger.activity


import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aneeq.messenger.R
import com.aneeq.messenger.util.ConnectionManager
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    lateinit var etPass: EditText
    lateinit var etEmail: EditText
    lateinit var btnSignIn: Button
    lateinit var txtPleaseSignUp: TextView
    lateinit var sharedPreferences: SharedPreferences

    companion object {
        const val REQUEST_CODE = 1234
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("Messenger Preferences", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        setContentView(R.layout.activity_sign_in)

        if (isLoggedIn) {
            val intent = Intent(this, LatestMessengerActivity::class.java)
            startActivity(intent)
            finish()

        }


        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Message From Developer")
        dialog.setMessage(
            "Hello , Thank you everyone for installing this Application.\n" +
                    "This App is still under development, and may have few bugs and unfinished Activities, so don't get panicked.Once the App has a big update,I'll let you know.\n" +
                    "In case the App crashes at some point, try restarting it, if not bring to my notice.\n" +
                    "The App is pretty much self explanatory. Try to explore the App yourself.\n" +
                    "I am always open to feedback and suggestions, so feel free to share your thoughts."
        )
        dialog.setPositiveButton("Yes, We Understand.") { _, _ ->
            dialog.show().cancel()

        }
        dialog.create()
        dialog.show()


        txtPleaseSignUp = findViewById(R.id.txtPleaseSignUp)
        etPass = findViewById(R.id.etPass)
        etEmail = findViewById(R.id.etEmail)
        btnSignIn = findViewById(R.id.btnSignIn)

        btnSignIn.setOnClickListener {
            if (ConnectionManager().checkConnection(this)) {
                verifyPermissions()

            }
            else {
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

        txtPleaseSignUp.setOnClickListener {
            val i = Intent(this, SignUpActivity::class.java)
            startActivity(i)
        }
    }


    private fun setUpSignIn() {
        val emailString = etEmail.text.toString()
        val passString = etPass.text.toString()
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(emailString, passString)
            .addOnCompleteListener {
                Toast.makeText(
                    this,
                    "successfully Logged in",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(
                    this,
                    LatestMessengerActivity::class.java
                )
                savePreferences()
                startActivity(intent)
                finishAffinity()
            }
            .addOnFailureListener {
                //
            }
    }

    fun savePreferences() {
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        sharedPreferences.edit().putString("pass", etPass.toString()).apply()
    }

    private fun verifyPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permissions[0]
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    permissions[1]
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    permissions[2]
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                setUpSignIn()
            } else {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        verifyPermissions()
    }
}
