package com.pixie.android.ui.draw.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.pixie.android.R
import com.pixie.android.model.chat.MessageData

class MessagingAdapter(context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private val listOfMessage = ArrayList<MessageData>()

    fun add(message: MessageData) {
        this.listOfMessage.add(message)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return listOfMessage.size
    }

    override fun getItem(position: Int): Any {
        return listOfMessage[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val message: MessageData = listOfMessage[position]

        return if (message.belongsToCurrentUser) {
            val rowView = inflater.inflate(R.layout.align_chat_right, parent, false)
            val txtTitle = rowView.findViewById<TextView>(R.id.text_title)
            txtTitle.text = message.text
            rowView
        } else {
            val rowView = inflater.inflate(R.layout.other_chat_message, parent, false)
            val txtTitle = rowView.findViewById<TextView>(R.id.message_body)
            val userName = rowView.findViewById<TextView>(R.id.name)
            txtTitle.text = message.text
            userName.text = message.userName
            rowView
        }
    }
}