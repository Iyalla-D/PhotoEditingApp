package com.example.photoeditor

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


@Suppress("DEPRECATION")
class Save_ShareActivity : AppCompatActivity() {

    private lateinit var _saveButton: ImageButton
    private lateinit var _shareButton: ImageButton
    private lateinit var _finalImageview: ImageView
    private var sentPhoto: Uri? = null
    private var _saveShareLayout: RelativeLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_share)

        _finalImageview = findViewById(R.id.finalImage)
        _saveButton = findViewById(R.id.saveButton)
        _shareButton = findViewById(R.id.shareButton)
        _saveShareLayout = findViewById(R.id.saveShareLayout)

        sentPhoto = intent.data
        with(_finalImageview) { setImageURI(sentPhoto) }

        _saveButton.setOnClickListener { saveMediaToStorage(_finalImageview.drawToBitmap()) }

        _shareButton.setOnClickListener { shareImage(_finalImageview.drawToBitmap()) }
    }

    private fun getImageUri(src: Bitmap): Uri? {
        val os = ByteArrayOutputStream()
        src.compress(CompressFormat.JPEG, 100, os)
        val path = MediaStore.Images.Media.insertImage(contentResolver, src, "title", null)
        return Uri.parse(path)
    }

    private fun shareImage(bitmap: Bitmap){
        val uri  = getImageUri(bitmap)
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM,uri)
        intent.type = "image/png"
        startActivity(Intent.createChooser(intent,"Share Via"))
    }

    private fun saveMediaToStorage(bitmap: Bitmap) {
        //Generating a file name
        val filename = "${System.currentTimeMillis()}.jpg"

        //Output stream
        var fos: OutputStream? = null

        //For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //getting the contentResolver
            contentResolver?.also { resolver ->

                //Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                //Inserting the contentValues to contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                //Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            //These for devices running on android < Q
            //So I don't think an explanation is needed here
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            bitmap.compress(CompressFormat.JPEG, 100, it)
            val snackBar: Snackbar = Snackbar.make(_saveShareLayout!!, "Saved to Photos", Snackbar.LENGTH_SHORT)
            snackBar.show()
        }
    }
}