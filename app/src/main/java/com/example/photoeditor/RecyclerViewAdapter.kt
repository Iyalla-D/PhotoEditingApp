package com.example.photoeditor

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView


class RecyclerViewAdapter(
    private val imageArrayList: ArrayList<RecyclerData>,
    private val mcontext: Context,
) :
    RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        // Inflate Layout
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.image_layout, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        // Set the data to textview and imageview.
        val recyclerData = imageArrayList[position]
        val byteArray: ByteArray? = recyclerData.getImage()
        val bmp: Bitmap = BitmapFactory.decodeByteArray(byteArray,0, byteArray!!.size  )
        holder.previouslyEdited.setImageBitmap(bmp)
        holder.previouslyEdited.setOnClickListener { view ->
            val byteArray2: ByteArray? = imageArrayList[position].getImage()
            val bmp2: Bitmap = BitmapFactory.decodeByteArray(byteArray2,0, byteArray2!!.size  )
            val intent = Intent(mcontext, EditActivity::class.java)
            intent.data = EditActivity.getImageUri(bmp2)
            mcontext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        // this method returns the size of recyclerview
        return imageArrayList.size
    }

    // View Holder Class to handle Recycler View.
    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val previouslyEdited: ImageButton

        init {
            previouslyEdited = itemView.findViewById(R.id.eachImage)
        }
    }
}