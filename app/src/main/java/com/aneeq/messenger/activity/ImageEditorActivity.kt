package com.aneeq.messenger.activity


import android.content.Intent
import android.graphics.Color
import android.graphics.Color.BLACK
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.ahmedadeltito.photoeditorsdk.BrushDrawingView
import com.ahmedadeltito.photoeditorsdk.PhotoEditorSDK
import com.aneeq.messenger.R
import com.aneeq.messenger.model.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import yuku.ambilwarna.AmbilWarnaDialog
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ImageEditorActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var radiog: RadioGroup
    lateinit var btnBrush: RadioButton
    lateinit var imgEditor: ImageView
    lateinit var etCaption: EditText
    lateinit var btnSend: ImageButton
    lateinit var skBrush: SeekBar
    lateinit var btnEraser: RadioButton
    lateinit var rlLayout: RelativeLayout
    lateinit var bdwimageEditor: BrushDrawingView
    var uri: Uri? = null
    var chat: ChatMessage? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_editor)

        radiog = findViewById(R.id.radiog)
        btnBrush = findViewById(R.id.btnBrush)
        rlLayout = findViewById(R.id.rlLayout)
        bdwimageEditor = findViewById(R.id.bdwimageEditor)
        toolbar = findViewById(R.id.toolbar)
        imgEditor = findViewById(R.id.imgEditor)
        etCaption = findViewById(R.id.etCaption)
        btnSend = findViewById(R.id.btnSend)
        skBrush = findViewById(R.id.skBrush)
        btnEraser = findViewById(R.id.btnEraser)
        setUptoolbar()
        setImage()
        imageEdit()
        btnSend.setOnClickListener {
            send()
        }

    }

    ///////////////////////////////////////////
    private fun imageEdit() {
        val psdk = PhotoEditorSDK.PhotoEditorSDKBuilder(this@ImageEditorActivity)
        psdk.parentView(rlLayout).childView(imgEditor)
            .buildPhotoEditorSDK()

        val brush = psdk.brushDrawingView(bdwimageEditor).buildPhotoEditorSDK()
        radiog.setOnCheckedChangeListener { p0, p1 ->
            when (p1) {
                R.id.btnBrush -> {
                    brush.setBrushDrawingMode(true)
                    val cp = AmbilWarnaDialog(
                        this,
                        R.color.colorAccent,
                        object : AmbilWarnaDialog.OnAmbilWarnaListener {
                            override fun onCancel(dialog: AmbilWarnaDialog?) {
                            }

                            override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                                brush.brushColor = color
                            }

                        })
                    cp.show()
                }
                R.id.btnEraser -> {
                    brush.brushEraser()
                    //brush.setBrushEraserColor(n)
                }
            }
        }


        skBrush.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                brush.brushSize = p1.toFloat()
                brush.setBrushEraserSize(p1.toFloat())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                //
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                //
            }

        })
    }

    /////////////////////////////////////


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.img_editor_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val psdk = PhotoEditorSDK.PhotoEditorSDKBuilder(this@ImageEditorActivity)
        psdk.parentView(rlLayout).childView(imgEditor)
            .buildPhotoEditorSDK()

        when (item.itemId) {
            R.id.imgEditUndo -> {
                psdk.buildPhotoEditorSDK().viewUndo()
            }
            R.id.imgEditEmoji -> {
                // psdk.buildPhotoEditorSDK().addEmoji()
            }
            R.id.imgEditText -> {
                val texty = psdk.buildPhotoEditorSDK()
                //texty.addText(text, colorCodeTextView);
            }
            R.id.imgEditSticker -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun setUptoolbar() {
        setSupportActionBar(toolbar)

    }

    private fun setImage() {
        uri = Uri.parse(intent.getStringExtra("sendImage"))
        Picasso.get().load(uri).error(R.drawable.appicob).into(imgEditor)
    }

    private fun setVideo() {
        uri = Uri.parse(intent.getStringExtra("sendVideo"))

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun send() {
        val fromId = FirebaseAuth.getInstance().uid!!
        val toId = intent!!.getStringExtra("sendto")
        val date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val filename = UUID.randomUUID().toString()
        val str = FirebaseStorage.getInstance()
            .getReference("/sentImages/$filename")
        str.putFile(uri!!)
            .addOnSuccessListener {
                str.downloadUrl.addOnSuccessListener {
                    val ref = FirebaseDatabase.getInstance()
                        .getReference("/user-messages/$fromId/$toId").push()
                    val reverseref =
                        FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId")
                            .push()
                    chat = ChatMessage(
                        ref.key.toString(), fromId, LatestMessengerActivity.currentUser!!.username,
                        "", it.toString(), toId!!,
                        date
                    )
                    ref.setValue(chat).addOnSuccessListener {
                        val i = Intent(this, ChatLogActivity::class.java)
                        i.putExtra("leID", toId)
                        startActivity(i)
                        Log.d("IE", "success")
                    }.addOnFailureListener { }
                    reverseref.setValue(chat)
                    val latestref =
                        FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
                    latestref.setValue(chat)
                    val latestreverseref =
                        FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
                    latestreverseref.setValue(chat)

                }.addOnFailureListener {}
                 }.addOnFailureListener {}




    }
}