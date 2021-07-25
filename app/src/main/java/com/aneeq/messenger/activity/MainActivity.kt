package com.aneeq.messenger.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.aneeq.messenger.R
import com.aneeq.messenger.overlay.FaceContourGraphic
import com.aneeq.messenger.overlay.GraphicOverlay
import com.aneeq.messenger.overlay.TextGraphic
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.text.TextRecognition
import com.squareup.picasso.Picasso
import java.io.IOException

class MainActivity : AppCompatActivity() {
    lateinit var btnSend: Button
    lateinit var txtml: TextView
    lateinit var imgml: ImageView
    var selectedPhoto: Uri? = null
    lateinit var btntxtrecog: Button
    lateinit var btnimglabel: Button
    lateinit var btndetface:Button
    var mGraphicOverlay: GraphicOverlay?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btndetface=findViewById(R.id.btndetface)
        btnSend = findViewById(R.id.btnSend)
        btntxtrecog = findViewById(R.id.btntxtrecog)
        txtml = findViewById(R.id.txtml)
        imgml = findViewById(R.id.imgml)
        mGraphicOverlay = findViewById(R.id.graphic_overlay)
        btnimglabel = findViewById(R.id.btnimglabel)
        btnSend.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhoto = data.data
            Picasso.get().load(selectedPhoto).into(imgml)
            btntxtrecog.setOnClickListener {
                recognizeText(selectedPhoto!!)
            }
            btnimglabel.setOnClickListener {
                labelImage(selectedPhoto!!)
            }
            btndetface.setOnClickListener{
                detectFace(selectedPhoto!!)
            }
        }
    }

    private fun recognizeText(selectedPhoto: Uri) {
        try {
            var text = ""
            val image = InputImage.fromFilePath(this, selectedPhoto)
            val recognizer = TextRecognition.getClient().process(image).addOnSuccessListener {
               // val resultText = it.text
                mGraphicOverlay!!.clear()
                for (block in it.textBlocks) {
                    val blockText=block.text
                    val blockFrame = block.boundingBox
                    val blockCornerPoints = block.cornerPoints
                    for(line in block.lines){
                        val lineText=line.text
                        val lineFrame = line.boundingBox
                        val lineCornerPoints = line.cornerPoints
                        for(element in line.elements){
                            val elementText=element.text
                            val elementFrame = element.boundingBox
                            val elementCornerPoints = element.cornerPoints

                            val textGraphic: GraphicOverlay.Graphic = TextGraphic(mGraphicOverlay,element)
                            mGraphicOverlay!!.add(textGraphic)
                            text = block.text
                            txtml.text = text


                        }
                    }

                }
            }.addOnFailureListener {}
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun labelImage(selectedPhoto: Uri) {
        var text = ""
        try {
            val image = InputImage.fromFilePath(this, selectedPhoto)
            val labeler =
                ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS).process(image)
                    .addOnSuccessListener { labels ->
                        for (label in labels) {
                            val confidence = label.confidence
                            val index = label.index
                            text += label.text
                            txtml.text = text
                        }
                    }
                    .addOnFailureListener { //
                    }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun detectFace(selectedPhoto: Uri) {
      /*  val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build() */
        mGraphicOverlay!!.clear()
        val faceopt=FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .build();

        try {
            val image = InputImage.fromFilePath(this, selectedPhoto)
            val detector = FaceDetection.getClient(faceopt).process(image)
                .addOnSuccessListener { faces ->
                    for (face in faces) {
                        val bounds = face.boundingBox
                        //val rotX=face.headEulerAngleX//Head is rotated up rotX degrees
                        //val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
                       // val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees

                        val faceGraphic = FaceContourGraphic(mGraphicOverlay)
                        mGraphicOverlay!!.add(faceGraphic)
                        faceGraphic.updateFace(face)

              // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                        // nose available):
                       // val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)!!.position

                        // If contour detection was enabled:
                        val leftEyeContour = face.getContour(FaceContour.LEFT_EYE)?.points
                        val upperLipBottomContour = face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points

                        // If classification was enabled:
                     /*   var smileProb: Float? = 0f
                        var rightEyeOpenProb: Float? = 0f
                        var leftEyeOpenProb: Float? = 0f
                        if (face.smilingProbability != null) {
                            smileProb = face.smilingProbability
                        }
                        if (face.rightEyeOpenProbability != null) {
                            rightEyeOpenProb = face.rightEyeOpenProbability
                        }
                        if (face.leftEyeOpenProbability != null) {
                            leftEyeOpenProb = face.leftEyeOpenProbability
                        }
                        txtml.text =
                            "Head is rotated to the right by $rotY degrees\n" +
                                    "Head is tilted sideways $rotZ degrees\n" +
                                    "Head is rotated upwards $rotX degrees\n" +
                                    "left Ear Position = $leftEar\n" +
                                    "Is He/She Smiling?=${smileProb!!.times(100)}% Yes\n" +
                                    "Is His/Hers Right Eye Open?=${rightEyeOpenProb!!.times(100)}% Yes\n" +
                                    "Is His/Hers Left Eye Open?=${leftEyeOpenProb!!.times(100)}% Yes" */
                        // If face tracking was enabled:
                        //if (face.trackingId != null) {
                        //    val id = face.trackingId
                        //  }
                    }

                }
                .addOnFailureListener {
                    // Task failed with an exception
                    // ...
                }


        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}