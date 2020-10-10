package com.pixie.android.ui.chat

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.utilities.InjectorUtils


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
        val messageList = root.findViewById<ListView>(R.id.messages_list)
        val editText = root.findViewById<EditText>(R.id.editText)

        val messageAdapter = MessagingAdapter(requireContext())
        val factory = InjectorUtils.provideChatViewModelFactory()

        val chatViewModel = ViewModelProvider(this, factory).get(ChatViewModel::class.java)

        messageList.adapter = messageAdapter

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

        val mainChannelMessageList = chatViewModel.getMainChannelMessage()
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


        return root
    }

}


