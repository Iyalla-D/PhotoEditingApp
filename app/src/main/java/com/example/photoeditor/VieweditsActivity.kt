package com.example.photoeditor

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar


class VieweditsActivity : AppCompatActivity() {
    var dbManager: SQLiteDatabase?= null
    //var dbManager1: DatabaseManger?= null
    private lateinit var recyclerView: RecyclerView
    private var recyclerDataArrayList: ArrayList<RecyclerData>? = null
    private lateinit var _noimage: TextView
    private  lateinit var _deleteButton: Button
    private var _historyLayout: RelativeLayout? = null

    //@SuppressLint("Range")
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewedits)

        _historyLayout = findViewById(R.id.historyLayout)
        _deleteButton = findViewById(R.id.deleteDatabase)
        _noimage = findViewById(R.id.noImages)
        _noimage.visibility = View.INVISIBLE
        recyclerView = findViewById(R.id.imagesRV)
        recyclerDataArrayList = ArrayList()
        dbManager = DatabaseManger.dbHelper!!.readableDatabase
        var byteArray: ByteArray? = null

        _deleteButton.setOnClickListener{
            var cursor: Cursor = MainActivity.dbManagerBase!!.fetch()
            if(cursor.moveToFirst()){
                do{
                    var id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.Picture_ID))
                    MainActivity.dbManagerBase!!.delete(id)
                }while (cursor.moveToNext())
            }
            val snackBar: Snackbar = Snackbar.make(_historyLayout!!, "History Cleared", Snackbar.LENGTH_LONG)
            snackBar.setAction("DISMISS") {
                snackBar.dismiss()
            }
            snackBar.show()

            val refresh = Intent(this, VieweditsActivity::class.java)
            startActivity(refresh) //Start the same Activity
            finish()
        }

        val columns = arrayOf(DatabaseHelper.Picture_ID,DatabaseHelper.Pictures)
        val cursor = dbManager!!.query(DatabaseHelper.Database_table,columns,null,null,null,null,null)
        //cursor?.moveToFirst()
        //var id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.Picture_ID))
        //MainActivity.dbManagerbase!!.delete(id)
        if(cursor.moveToFirst()){
            do{
                byteArray = cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.Pictures))
                recyclerDataArrayList!!.add(RecyclerData(byteArray!!))

            }while (cursor.moveToNext())
        }
        else{
            _noimage.visibility = View.VISIBLE
            recyclerView.visibility = View.INVISIBLE
            _deleteButton.visibility = View.INVISIBLE
        }
        val adapter = RecyclerViewAdapter(recyclerDataArrayList!!, this)

        val layoutManager = GridLayoutManager(this, 3)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }


}