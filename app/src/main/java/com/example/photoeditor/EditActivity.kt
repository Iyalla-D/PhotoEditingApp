package com.example.photoeditor

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap


class EditActivity : AppCompatActivity() {

    private lateinit var _editimageview: ImageView
    private lateinit var _drawingPen: ImageButton
    private lateinit var _rotateImage: ImageButton
    private lateinit var _confirmImage: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)



        _editimageview = findViewById<ImageView>(R.id.editimage)
        var bytes = intent.getByteArrayExtra("imageBitmap")
        val bitmap = bytes?.let { BitmapFactory.decodeByteArray(bytes,0, it.size) }
        _editimageview.setImageBitmap(bitmap)

        _drawingPen = findViewById<ImageButton>(R.id.drawButton)
        _rotateImage = findViewById<ImageButton>(R.id.rotateImageButton)
        _confirmImage = findViewById<ImageButton>(R.id.checkmark)
        var _text = findViewById<TextView>(R.id.textTest)



        _drawingPen.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                _text.setText("pen")
            }
        })

        _rotateImage.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val matrix = Matrix()
                matrix.postRotate(90F)
                val imagebitmap = _editimageview.drawToBitmap()
                val rotated = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.width, imagebitmap.height, matrix, true)
                _editimageview.setImageBitmap(rotated)
            }
        })

        _confirmImage.setOnClickListener{
            var intent: Intent = Intent(this, Save_ShareActivity::class.java)
            startActivity(intent)
        }
    }



}