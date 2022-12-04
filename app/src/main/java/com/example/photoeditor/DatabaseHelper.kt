package com.example.photoeditor

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, Database_Name, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(Create_DB_Query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $Database_table")
        onCreate(db)
    }

    companion object {
        // Table Name
        const val Database_table = "Pictures"

        // Table columns
        const val Picture_ID: String = "_ID"
        const val Pictures = "edited_pictures"
        //const val DESC = "description"

        // Database Information
        const val Database_Name = "Edited_Pictures"

        // database version
        const val DB_VERSION = 1

        // Creating table query
        private const val Create_DB_Query: String = "CREATE TABLE IF NOT EXISTS $Database_table($Picture_ID INTEGER PRIMARY KEY AUTOINCREMENT,$Pictures BLOB);"
    }
}