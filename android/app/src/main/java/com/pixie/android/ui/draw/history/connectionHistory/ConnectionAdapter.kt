package com.pixie.android.ui.draw.history.connectionHistory

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.pixie.android.R
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.history.ConnectionHistory

class ConnectionAdapter (context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    val context =context
    private var listOfConnection = ArrayList<ConnectionHistory>()

    fun set(newConnectionList:ArrayList<ConnectionHistory>?){
        if(newConnectionList!=null) {
            listOfConnection= ArrayList(newConnectionList)
        }
        notifyDataSetChanged()
    }

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
        val history: ConnectionHistory = listOfConnection[listOfConnection.size - position - 1]
        val rowView = inflater.inflate(R.layout.connection_history_row, parent, false)
        val connection = rowView.findViewById<TextView>(R.id.connection)
        val disconnection = rowView.findViewById<TextView>(R.id.disconnection)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")

        var dateConnection = context.resources.getString(R.string.not_available)
        var dateDisconnection = context.resources.getString(R.string.not_disconnected)
        if(history.connection != "null") {
            dateConnection = getDate(format.parse(history.connection).time, "yyyy-MM-dd HH:mm:ss")
        }
        if(history.disconnection != "null") {
            dateDisconnection = getDate(format.parse(history.disconnection).time, "yyyy-MM-dd HH:mm:ss")
        }
        connection.text = dateConnection
        disconnection.text = dateDisconnection

        if((position % 2) != 0){
            connection.background = ContextCompat.getDrawable(context, R.drawable.history_table_input_border_blue)
            disconnection.background = ContextCompat.getDrawable(context, R.drawable.history_table_input_border_blue)
        }

        return rowView

    }

    private fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }
}