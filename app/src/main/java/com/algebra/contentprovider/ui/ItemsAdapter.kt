package com.algebra.contentprovider.ui

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cursoradapter.widget.CursorAdapter
import com.algebra.contentprovider.R
import com.algebra.contentprovider.database.TasksContract

// import com.algebra.contentprovider.database.TasksContract

class ItemsAdapter(context: Context, cursor: Cursor, flags: Int) : CursorAdapter(context, cursor, flags) {

    private val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        return layoutInflater.inflate(R.layout.task_item, parent, false)
    }

    @SuppressLint( "Range" )
    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        val tvName: TextView = view?.findViewById(R.id.tvName)!!
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)

        val taskName = cursor?.getString( cursor.getColumnIndex( TasksContract.Columns.TASK_NAME ) )
        val taskDescripton = cursor?.getString( cursor.getColumnIndex( TasksContract.Columns.TASK_DESCRIPTION ) )

        tvName.text        = taskName
        tvDescription.text = taskDescripton
    }

}