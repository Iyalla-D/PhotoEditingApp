package com.example.photoeditor

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import java.io.File


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    companion object {
        var dbManagerBase: DatabaseManger?= null

    }

    private var pickedBitMap: Bitmap? = null
    private lateinit var _cameraButton:Button
    private lateinit var _editbutton:Button
    private lateinit var _getphotobutton:Button
    private lateinit var _viewEdits:Button
    private lateinit var _imageview: ImageView
    private var _mainLayout: RelativeLayout? = null
    private var _uri: Uri? = null
    private var pickedPhoto: Uri? = null

    private var cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == RESULT_OK) {
                // get the image and set it on the image view by using the bitmap factor to decode the
                //jpg
                val file = File(getExternalFilesDir(null), "test.jpg")
                val options: BitmapFactory.Options = BitmapFactory.Options()
                val image: Bitmap = BitmapFactory.decodeFile(file.path, options)
                val matrix = Matrix()
                matrix.postRotate(90F)
                val rotated = Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)
                pickedBitMap=rotated
                _imageview.setImageBitmap(rotated)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbManagerBase = DatabaseManger(this)
        try {
            dbManagerBase!!.open()
        }
        catch (e: Exception){
            e.stackTrace
        }


        //val config: Configuration = resources.configuration
        //if(config.orientation == Configuration.ORIENTATION_PORTRAIT)
            //setContentView(R.layout.activity_main)
        //else if(config.orientation == Configuration.ORIENTATION_LANDSCAPE)
            //setContentView(R.layout.activity_main_landscape)

        _cameraButton = findViewById(R.id.camera)
        _getphotobutton = findViewById(R.id.getphoto)
        _imageview = findViewById(R.id.image)
        _editbutton = findViewById(R.id.editphoto)
        _viewEdits = findViewById(R.id.viewEdits)
        _mainLayout = findViewById(R.id.main_layout)

        _viewEdits.setOnClickListener{
            val intent = Intent(this, VieweditsActivity::class.java)
            startActivity(intent)
        }

        _cameraButton.setOnClickListener { startCameraActivity() }

        _getphotobutton.setOnClickListener { choosePhotoActivity() }

        _editbutton.setOnClickListener{
            if(pickedBitMap==null){
                val snackBar: Snackbar = Snackbar.make(_mainLayout!!, "No Photo Picked", Snackbar.LENGTH_LONG)
                snackBar.setAction("DISMISS") {
                    snackBar.dismiss()
                }
                snackBar.show()
            }
            else {
                val intent = Intent(this, EditActivity::class.java)
                if(_uri != null){
                    intent.data = _uri
                    startActivity(intent)
                }
                else{
                    intent.data = pickedPhoto
                    startActivity(intent)
                }
            }
        }
    }

    private fun choosePhotoActivity() {
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }
        else{
            val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent,2)
        }
        _uri = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if(requestCode==1){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @Deprecated("Deprecated in Java")
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
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file = File(getExternalFilesDir(null), "test.jpg")
        _uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, _uri)
        cameraResultLauncher.launch(intent)
    }

}




