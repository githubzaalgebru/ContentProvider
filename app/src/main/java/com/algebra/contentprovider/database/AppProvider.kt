package com.algebra.contentprovider.database

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log
import java.sql.SQLException

const val CONTENT_AUTHORITY = "com.algebra.tasks.provider"

private const val TASKS = 100
private const val TASKS_ID = 101

val CONTENT_AUTHORITY_URI: Uri = Uri.parse( "content://$CONTENT_AUTHORITY" )

class AppProvider : ContentProvider( ) {

    private val TAG = "AppProvider"

    private val uriMatcher by lazy { buildUriMatcher() }

    private fun buildUriMatcher(): UriMatcher {
        Log.d(TAG, "buildUriMatcher: starts")
        val matcher = UriMatcher( UriMatcher.NO_MATCH )

        // e.g. content://com.algebra.tasks.provider/Tasks
        matcher.addURI(
            CONTENT_AUTHORITY,
            TasksContract.TABLE_NAME,
            TASKS
        )

        // e.g. content://com.algebra.tasks.provider/Tasks/8
        matcher.addURI(
            CONTENT_AUTHORITY, "${TasksContract.TABLE_NAME}/#",
            TASKS_ID
        )

        return matcher
    }

    override fun onCreate( ): Boolean {
        Log.i( TAG, "AppProvider created" )
        return true
    }

    override fun getType( uri: Uri ): String? {
        val match = uriMatcher.match( uri )

        return when ( match ) {
            TASKS -> TasksContract.CONTENT_TYPE
            TASKS_ID -> TasksContract.CONTENT_ITEM_TYPE
            else -> throw IllegalArgumentException("unknown Uri: $uri")
        }
    }

    override fun insert( uri: Uri, values: ContentValues? ): Uri? {
        Log.d( TAG, "insert: called with uri $uri" )
        val match = uriMatcher.match( uri )
        Log.d( TAG, "insert: match is $match" )

        val recordId: Long
        val returnUri: Uri

        when ( match ) {
            TASKS -> {
                val db = context?.let { AppDatabase.getInstance( it ).writableDatabase }
                recordId = db?.insert( TasksContract.TABLE_NAME, null, values ) ?: 0L
                if ( recordId != -1L ) {
                    returnUri = TasksContract.buildUriFromId( recordId )
                } else {
                    throw SQLException( "Failed to insert, Uri was $uri" )
                }
            }
            else -> throw IllegalArgumentException("Unknown uri: $uri")
        }

        Log.d( TAG, "Exiting insert, returning $returnUri" )
        return returnUri
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        Log.d(TAG, "query: called with uri $uri")
        val match = uriMatcher.match( uri )
        Log.d(TAG, "query: match is $match")

        val queryBuilder = SQLiteQueryBuilder( )

        when ( match ) {
            TASKS -> queryBuilder.tables = TasksContract.TABLE_NAME

            TASKS_ID -> {
                queryBuilder.tables = TasksContract.TABLE_NAME
                val taskId = TasksContract.getId( uri )
                queryBuilder.appendWhere("${TasksContract.Columns.ID} = ")
                queryBuilder.appendWhereEscapeString("$taskId")
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        val db = context?.let { AppDatabase.getInstance(it).readableDatabase }
        val cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder)
        Log.d(TAG, "query: rows in returned cursor = ${cursor.count}")

        return cursor
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        Log.d(TAG, "update: called with uri $uri")
        val match = uriMatcher.match(uri)
        Log.d(TAG, "update: match is $match")

        val count: Int
        var selectionCriteria: String

        when (match) {

            TASKS -> {
                val db = context?.let { AppDatabase.getInstance(it).writableDatabase }
                count = db?.update( TasksContract.TABLE_NAME, values, selection, selectionArgs ) ?: 0
            }

            TASKS_ID -> {
                val db = context?.let { AppDatabase.getInstance(it).writableDatabase }
                val id = TasksContract.getId(uri)
                selectionCriteria = "${TasksContract.Columns.ID} = $id"

                if (selection != null && selection.isNotEmpty()) {
                    selectionCriteria += " AND ( $selection )"
                }

                count = db?.update(TasksContract.TABLE_NAME, values, selectionCriteria, selectionArgs) ?: 0
            }
            else -> throw IllegalArgumentException("Unknown uri: $uri")
        }

        Log.d(TAG, "Exiting update, returning $count")
        return count
    }

    override fun delete( uri: Uri, selection: String?, selectionArgs: Array<String>? ): Int {
        Log.d(TAG, "delete: called with uri $uri")
        val match = uriMatcher.match(uri)
        Log.d(TAG, "delete: match is $match")

        val count: Int
        var selectionCriteria: String

        when (match) {

            TASKS -> {
                val db = context?.let { AppDatabase.getInstance(it).writableDatabase }
                count = db?.delete(TasksContract.TABLE_NAME, selection, selectionArgs) ?: 0
            }

            TASKS_ID -> {
                val db = context?.let { AppDatabase.getInstance(it).writableDatabase }
                val id = TasksContract.getId(uri)
                selectionCriteria = "${TasksContract.Columns.ID} = $id"

                if (selection != null && selection.isNotEmpty()) {
                    selectionCriteria += " AND ($selection)"
                }
                count = db?.delete(TasksContract.TABLE_NAME, selectionCriteria, selectionArgs) ?: 0
            }
            else -> throw IllegalArgumentException("Unknown uri: $uri")
        }

        Log.d(TAG, "Exiting update, returning $count")
        return count
    }
}