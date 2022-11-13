package com.example.photoeditor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class EditActivity : AppCompatActivity() {

    private lateinit var _editimageview: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        _editimageview = findViewById<ImageView>(R.id.editimage)
        val bundle: Bundle = intent.extras!!
        val editimage: Int = bundle.getInt("Image")
        _editimageview.setImageResource(editimage)
    }


}