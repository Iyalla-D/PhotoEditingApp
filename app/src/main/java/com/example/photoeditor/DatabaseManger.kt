package com.example.photoeditor

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

class DatabaseManger(ctx:Context) {
    var context: Context? = ctx
    private var database: SQLiteDatabase? = null

    fun open(): DatabaseManger {
        dbHelper = DatabaseHelper(context)
        database = dbHelper!!.writableDatabase
        return this
    }

    fun close(){
        dbHelper?.close()
    }

    fun insert(bitmap: Bitmap){
        val contentValues = ContentValues()
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray: ByteArray = stream.toByteArray()
        contentValues.put(DatabaseHelper.Pictures,byteArray)
        database?.insert(DatabaseHelper.Database_table,null,contentValues)

    }


    fun fetch(): Cursor{
        val columns = arrayOf(DatabaseHelper.Picture_ID,DatabaseHelper.Pictures)
        val cursor: Cursor = database!!.query(DatabaseHelper.Database_table,columns,null,null,null,null,null)
        if(cursor!=null){
            cursor.moveToFirst()
        }

        return cursor
    }

    fun update(_id: Long, picture: ByteArray): Int {
        val contentValues = ContentValues()
        contentValues.put(DatabaseHelper.Pictures, picture)
        return database!!.update(DatabaseHelper.Database_table, contentValues, DatabaseHelper.Picture_ID+"="+_id, null)
    }

    fun delete(_id: Long){
        database!!.delete(DatabaseHelper.Database_table,DatabaseHelper.Picture_ID+"="+_id, null)
    }

    companion object{
        var dbHelper: DatabaseHelper? = null
    }
}
