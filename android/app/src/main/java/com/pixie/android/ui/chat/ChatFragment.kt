package com.pixie.android.ui.chat

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.pixie.android.R
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
        val participantListElement = root.findViewById<ListView>(R.id.participant_list)
        val chatTab = root.findViewById<TabLayout>(R.id.chat_tab)
        val editText = root.findViewById<EditText>(R.id.editText)

        val messageAdapter = MessagingAdapter(requireContext())
        val participantAdapter = ChannelParticipantAdapter(requireContext())
        val factory = InjectorUtils.provideChatViewModelFactory()

        val chatViewModel = ViewModelProvider(this, factory).get(ChatViewModel::class.java)

        messageList.adapter = messageAdapter
        participantListElement.adapter = participantAdapter


        sendMessage.setOnClickListener {
            val message = editText.text.toString()

            if (message.isNotBlank()) {
                chatViewModel.sendMessageToCurrentChannel(message)
                editText.text.clear() //clear text line

            }
        }

        // Enter button on real keyboard if attached to android
        editText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER) {
                    val message = editText.text.toString()
                    if (message.isNotBlank()) {
                        chatViewModel.sendMessageToCurrentChannel(message)
                        editText.text.clear() //clear text line
                    }
                    return true
                }
                return false
            }
        })

        // Enter button on soft keyboard
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val message = editText.text.toString()

                if (message.isNotBlank()) {
                    chatViewModel.sendMessageToCurrentChannel(message)
                    editText.text.clear()
                }
                true
            } else {
                false
            }
        }

        chatTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Do nothing
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //Do nothing
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {

                if (tab?.position ==1){ // Active Users
                    messageLayout.visibility= View.INVISIBLE
                    participant_list.visibility = View.VISIBLE
                }else if (tab?.position ==0){ // Messages
                    messageLayout.visibility= View.VISIBLE
                    participant_list.visibility = View.INVISIBLE
                }
            }

        })
        val mainChannelMessageList = chatViewModel.getMainChannelMessage()
        val mainChannelParticipantList = chatViewModel.getMainChannelParticipants()
        mainChannelMessageList.observe(viewLifecycleOwner, Observer {messageList->
            if (!messageList.isNullOrEmpty()){
                if(messageAdapter.isEmpty){
                    // Repopulating the adapter
                    messageList.forEach {
                        messageAdapter.add(it)
                    }

                }else{
                    messageAdapter.add(messageList.last())
                }
            }

        })
        mainChannelParticipantList.observe(viewLifecycleOwner, Observer { participantList->
            if(!participantList.isNullOrEmpty()){
                participantAdapter.clear()
                participantList.forEach{
                    participantAdapter.add(it)
                }

            }

        })


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Participant list is initially hidden

        participant_list.visibility = View.INVISIBLE

        super.onViewCreated(view, savedInstanceState)
    }

}


