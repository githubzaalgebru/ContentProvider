package com.algebra.contentprovider

import android.content.ContentValues
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.algebra.contentprovider.database.TasksContract
import com.algebra.contentprovider.ui.ItemsAdapter

class MainActivity : AppCompatActivity( ) {

    private lateinit var tvTaskId          : TextView
    private lateinit var etTaskName        : EditText
    private lateinit var etTaskDescription : EditText
    private lateinit var bAdd              : Button
    private lateinit var bUpdate           : Button
    private lateinit var bDelete           : Button
    private lateinit var lvResult          : ListView
    private lateinit var adapter           : ItemsAdapter


    override fun onCreate( savedInstanceState: Bundle? ) {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.activity_main )

        initWidgets( )
        setupListeners( )
    }

    fun initWidgets( ) {
        tvTaskId          = findViewById( R.id.tvTaskId )
        etTaskName        = findViewById( R.id.etTaskName )
        etTaskDescription = findViewById( R.id.etTaskDescription )
        bAdd              = findViewById( R.id.bAdd )
        bDelete           = findViewById( R.id.bDelete )
        bUpdate           = findViewById( R.id.bEdit )
        lvResult          = findViewById( R.id.lvResult )

        val cursor = contentResolver.query( TasksContract.CONTENT_URI, null, null, null, null )
        adapter = cursor?.let { ItemsAdapter( this@MainActivity, it, 0 ) }!!
        lvResult.adapter = adapter

    }

    fun setupListeners( ) {
        bAdd.setOnClickListener {
            val contentValues = ContentValues( )
            contentValues.put( TasksContract.Columns.TASK_NAME, etTaskName.text.toString( ) )
            contentValues.put( TasksContract.Columns.TASK_DESCRIPTION, etTaskDescription.text.toString( ) )
            //content provider i metode za unos podataka pozivamo preko objekta content resolver
            contentResolver.insert( TasksContract.CONTENT_URI, contentValues )
            refreshData( )
            clearFields( )
        }

        bUpdate.setOnClickListener {
            val id = tvTaskId.text.trim( )
            if( id!=null && id!="") {
                val contentValues = ContentValues()
                contentValues.put(TasksContract.Columns.TASK_NAME, etTaskName.text.toString())
                contentValues.put(TasksContract.Columns.TASK_DESCRIPTION, etTaskDescription.text.toString())
                //selection je izraz koji ide u sql where clause, u ovom izrazu selection je where  id = ?
                // -->upitnik ce zamijeniti vrijednost koja se šalje u arrayu selectionArgs
                contentResolver.update(
                    Uri.parse(TasksContract.CONTENT_URI.toString() + "/" + id),
                    contentValues,
                    null,
                    null
                )
                refreshData( )
                clearFields( )
            }
        }

        bDelete.setOnClickListener {
            val id = tvTaskId.text.trim( )
            if( id!=null && id!="") {
                contentResolver.delete( Uri.parse(TasksContract.CONTENT_URI.toString( ) + "/" + id), null, null )
                refreshData( )
                clearFields( )
            }
        }

        lvResult.setOnItemClickListener { adapterView, view, i, l ->
            val id   = "$l"
            val name = view.findViewById< TextView >( R.id.tvName ).text
            val description = view.findViewById< TextView >( R.id.tvDescription ).text
            tvTaskId.text = id
            etTaskName.setText( name )
            etTaskDescription.setText( description )

            // contentResolver.delete( Uri.parse( TasksContract.CONTENT_URI.toString( ) + id ), null, null )
            // refreshData( )
        }
    }

    private fun refreshData( ) {
        val cursor = contentResolver.query( TasksContract.CONTENT_URI, null, null, null, null )
        adapter.swapCursor( cursor )
    }

    private fun clearFields( ) {
        tvTaskId.text = ""
        etTaskName.setText( "" )
        etTaskDescription.setText( "" )
    }
}