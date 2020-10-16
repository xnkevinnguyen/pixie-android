package com.pixie.android.ui.chat

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.pixie.android.R
import com.pixie.android.model.chat.MessageData
import com.pixie.android.utilities.Constants


class MessagingAdapter(context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var listOfMessage = ArrayList<MessageData>()

    fun add(message: MessageData) {
        this.listOfMessage.add(message)
        notifyDataSetChanged()

    }
    fun set(newMessages:ArrayList<MessageData>){
        listOfMessage = ArrayList(newMessages)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return listOfMessage.size
    }

    override fun getItem(position: Int): Any {
        return listOfMessage[position]
    }
    fun clear(){
        listOfMessage.clear()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("DefaultLocale")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val message: MessageData = listOfMessage[position]
        val messageBelongsToCurrentUser = message.belongsToCurrentUser
        return if (messageBelongsToCurrentUser==true) {

            val rowView = inflater.inflate(R.layout.align_chat_right, parent, false)
            val txtTitle = rowView.findViewById<TextView>(R.id.text_title)
            val timePosted = rowView.findViewById<TextView>(R.id.time)
            val time = message.timePosted.toLong()
            val timeFormatted = getDate(time, "HH:mm:ss")
            txtTitle.text = message.text
            timePosted.text = timeFormatted

            rowView

        } else {
            val rowView = inflater.inflate(R.layout.other_chat_message, parent, false)
            val txtTitle = rowView.findViewById<TextView>(R.id.message_body)
            val userName = rowView.findViewById<TextView>(R.id.name)
            val timePosted = rowView.findViewById<TextView>(R.id.time)
            val time = message.timePosted.toLong()
            val timeFormatted = getDate(time, "HH:mm:ss")

            txtTitle.text = message.text
            userName.text = message.userName
            timePosted.text = timeFormatted
            rowView
        }
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