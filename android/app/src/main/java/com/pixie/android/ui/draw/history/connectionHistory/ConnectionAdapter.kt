package com.pixie.android.ui.draw.history.connectionHistory

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.pixie.android.R
import com.pixie.android.model.history.ConnectionHistory

class ConnectionAdapter (context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var listOfConnection = ArrayList<ConnectionHistory>()

    fun add(connectionHistory: ConnectionHistory) {
        this.listOfConnection.add(connectionHistory)
        notifyDataSetChanged()
    }
    fun clear(){
        listOfConnection.clear()
    }

    override fun getCount(): Int {
        return listOfConnection.size
    }

    override fun getItem(position: Int): Any {
        return listOfConnection[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val history: ConnectionHistory = listOfConnection[position]
        val rowView = inflater.inflate(R.layout.connection_history_row, parent, false)
        val connection = rowView.findViewById<TextView>(R.id.connection)
        val disconnection = rowView.findViewById<TextView>(R.id.disconnection)
        connection.text = history.connexion
        disconnection.text = history.disconnection
        return rowView

    }
}