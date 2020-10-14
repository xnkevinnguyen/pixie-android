package com.pixie.android.ui.chat

import android.annotation.SuppressLint
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.pixie.android.R
import com.pixie.android.model.chat.MessageData

class GuessAdapter (context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var listOfGuess = ArrayList<MessageData>()

    fun add(message: MessageData) {
        this.listOfGuess.add(message)
        notifyDataSetChanged()
    }
    fun set(newMessages:ArrayList<MessageData>){
        listOfGuess = ArrayList(newMessages)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return listOfGuess.size
    }

    override fun getItem(position: Int): Any {
        return listOfGuess[position]
    }
    fun clear(){
        listOfGuess.clear()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("DefaultLocale")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val message: MessageData = listOfGuess[position]
        val rowView = inflater.inflate(R.layout.align_chat_right, parent, false)
        val txtTitle = rowView.findViewById<TextView>(R.id.text_title)
        val timePosted = rowView.findViewById<TextView>(R.id.time)
        val time = message.timePosted.toLong()
        val timeFormatted = getDate(time, "HH:mm:ss")


        txtTitle.text = message.text
        timePosted.text = timeFormatted

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