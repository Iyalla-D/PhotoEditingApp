package com.example.photoeditor

import android.app.Activity
import android.content.res.Configuration
import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import java.io.File
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.os.Build
import android.view.Window
import android.widget.RelativeLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import com.google.android.material.snackbar.Snackbar
import android.content.Intent as Intent


class MainActivity : AppCompatActivity() {

    var pickedPhoto: Uri? = null
    var pickedBitMap: Bitmap? = null
    private lateinit var _button:Button
    private lateinit var _editbutton:Button
    private lateinit var _getphotobutton:Button
    private lateinit var _imageview: ImageView
    private var _mainLayout: RelativeLayout? = null
    private var _uri: Uri? = null

    private var cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == RESULT_OK) {
                // get the image and set it on the image view by using the bitmap factor to decode the
                //jpg
                var file: File = File(getExternalFilesDir(null), "test.jpg")
                var options: BitmapFactory.Options = BitmapFactory.Options()
                var image: Bitmap = BitmapFactory.decodeFile(file.path, options)
                pickedBitMap=image
                _imageview.setImageBitmap(image)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var config: Configuration = resources.configuration
        if(config.orientation == Configuration.ORIENTATION_PORTRAIT)
            setContentView(R.layout.activity_main)
        else if(config.orientation == Configuration.ORIENTATION_LANDSCAPE)
            setContentView(R.layout.activity_main_landscape)

        _button = findViewById<Button>(R.id.camera)
        _getphotobutton = findViewById<Button>(R.id.getphoto)
        _imageview = findViewById<ImageView>(R.id.image)
        _editbutton = findViewById<Button>(R.id.editphoto)
        _mainLayout = findViewById<RelativeLayout>(R.id.main_layout)


        _button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                startCameraActivity()
            }
        })

        _getphotobutton.setOnClickListener(object : View.OnClickListener  {
            override fun onClick(p0: View?) {
               choosePhotoActivity();
            }
        })

        _editbutton.setOnClickListener{
            if(pickedBitMap==null){
                var snackBar: Snackbar = Snackbar.make(_mainLayout!!, "No Photo Picked", Snackbar.LENGTH_LONG)
                snackBar.setAction("DISMISS", View.OnClickListener {
                    snackBar.dismiss()
                })
                snackBar.show()
            }
            else {
                var intent: Intent = Intent(this, EditActivity::class.java)
                var stream = ByteArrayOutputStream();
                pickedBitMap?.compress(Bitmap.CompressFormat.JPEG, 25, stream)
                var bytes = stream.toByteArray()
                intent.putExtra("imageBitmap", bytes)
                startActivity(intent)
            }
        }
    }

    private fun choosePhotoActivity() {
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }
        else{
            var intent: Intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent,2)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==1){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode ==2 && resultCode==Activity.RESULT_OK && data!=null){
            pickedPhoto = data.data
            if(pickedPhoto!= null){
                if(Build.VERSION.SDK_INT>=28){
                    val source = ImageDecoder.createSource(this.contentResolver,pickedPhoto!!)
                    pickedBitMap = ImageDecoder.decodeBitmap(source)
                    _imageview.setImageBitmap(pickedBitMap)
                }
                else{
                    pickedBitMap = MediaStore.Images.Media.getBitmap(this.contentResolver,pickedPhoto)
                    _imageview.setImageBitmap(pickedBitMap)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun startCameraActivity() {
        // setup an intent to ask the android system to fire up an app to capture an image for us
        var intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var file: File = File(getExternalFilesDir(null), "test.jpg")
        _uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, _uri)
        cameraResultLauncher.launch(intent)
    }

}

private fun Intent.putExtra(s: String, _imageview: ImageView?) {

}
