package com.pixie.android.ui.draw.chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.pixie.android.R
import com.pixie.android.model.draw.MessageData
import kotlinx.android.synthetic.main.chat_fragment.*


class ChatFragment() : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(
            R.layout.chat_fragment,
            container, false
        )

        val sendMessage = root.findViewById<ImageButton>(R.id.send_message)
        val messageList = root.findViewById<ListView>(R.id.messages_view)
        val adapter = RecipeAdapter(requireContext())
        messageList.adapter = adapter

        sendMessage.setOnClickListener {
            val message = editText.text.toString()
            val messageData = MessageData(message, belongsToCurrentUser = true)
            if(message.isNotBlank()){
                adapter.add(messageData)
                editText.text.clear() //clear text line
            }
        }

        // when receive message
        // 1. create data MessageData -Text, belongsToCurrentUser=false, Username= ...
        // 2. adapter.add(MessageData created in 1)
        return root
    }
}

class RecipeAdapter(private val context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

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

        return if(message.belongsToCurrentUser){
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