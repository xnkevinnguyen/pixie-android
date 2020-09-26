package com.pixie.android.ui.draw.chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.pixie.android.R
import com.pixie.android.model.chat.MessageData
import com.pixie.android.utilities.InjectorUtils
import kotlinx.android.synthetic.main.chat_fragment.*


class ChatFragment : Fragment() {
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
        val messageLayout = root.findViewById<LinearLayout>(R.id.message_layout)
        val messageList = root.findViewById<ListView>(R.id.messages_list)
        val chatTab = root.findViewById<TabLayout>(R.id.chat_tab)

        val adapter = RecipeAdapter(requireContext())
        val factory = InjectorUtils.provideChatViewModelFactory()

        val chatViewModel = ViewModelProvider(this, factory).get(ChatViewModel::class.java)

        messageList.adapter = adapter

        sendMessage.setOnClickListener {
            val message = editText.text.toString()

            if (message.isNotBlank()) {
                chatViewModel.sendMessageToCurrentChannel(message)
                editText.text.clear() //clear text line

            }
        }
        chatTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                onTabReselected(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                onTabUnselected(tab)
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {

                if (tab?.position ==1){ // Active Users
                    messageLayout.visibility= View.INVISIBLE
                    users_list.visibility = View.VISIBLE
                }else if (tab?.position ==0){ // Messages
                    messageLayout.visibility= View.VISIBLE
                    users_list.visibility = View.INVISIBLE
                }
            }

        })
        val mainChannelMessageList = chatViewModel.getMainChannelMessage()
        mainChannelMessageList.observe(viewLifecycleOwner, Observer {messageList->
            if (!messageList.isNullOrEmpty()){
                if(adapter.isEmpty){
                    // Repopulating the adapter
                    messageList.forEach {
                        adapter.add(it)
                    }

                }else{
                    adapter.add(messageList.last())
                }
            }

        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val factory = InjectorUtils.provideChatViewModelFactory()

        val chatViewModel = ViewModelProvider(this, factory).get(ChatViewModel::class.java)
        chatViewModel.suscribeToChannel()
        super.onViewCreated(view, savedInstanceState)
    }
}


class RecipeAdapter(context: Context) : BaseAdapter() {

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