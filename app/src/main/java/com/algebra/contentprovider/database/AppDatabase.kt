package com.algebra.contentprovider.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

private const val DATABASE_NAME = "TaskTimer.db"
private const val DATABASE_VERSION = 1

class AppDatabase private constructor( context : Context ) : SQLiteOpenHelper( context, DATABASE_NAME, null, DATABASE_VERSION ) {

    private val TAG = "AppDatabase"

    override fun onCreate( db: SQLiteDatabase ) {
        Log.d(TAG, "onCreate: starts")
        val sSQL = """CREATE TABLE ${ TasksContract.TABLE_NAME} (
            ${ TasksContract.Columns.ID } INTEGER PRIMARY KEY NOT NULL,
            ${ TasksContract.Columns.TASK_NAME } TEXT NOT NULL,
            ${ TasksContract.Columns.TASK_DESCRIPTION } TEXT);""".replaceIndent(" ")
        Log.d( TAG, sSQL )
        db.execSQL(sSQL)
        Log.d( TAG, "onCreate: ends" )
    }

    override fun onUpgrade( db: SQLiteDatabase?, oldVersion: Int, newVersion: Int ) {
        TODO( "Not yet implemented" )
    }

    companion object : SingletonHolder < AppDatabase, Context >( ::AppDatabase )
}