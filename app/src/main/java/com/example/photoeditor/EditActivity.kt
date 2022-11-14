package com.example.photoeditor

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class EditActivity : AppCompatActivity() {

    private lateinit var _editimageview: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)



        _editimageview = findViewById<ImageView>(R.id.editimage)
        var bytes = intent.getByteArrayExtra("imageBitmap")
        val bitmap = bytes?.let { BitmapFactory.decodeByteArray(bytes,0, it.size) }
        _editimageview.setImageBitmap(bitmap)
    }


}