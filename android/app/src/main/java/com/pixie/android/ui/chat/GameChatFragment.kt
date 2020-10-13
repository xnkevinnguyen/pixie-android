package com.pixie.android.ui.chat

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils


class GameChatFragment : Fragment() {
    private lateinit var preferencesGame: SharedPreferences
    private lateinit var editorGame: SharedPreferences.Editor
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(
            R.layout.game_chat_fragment,
            container, false
        )

        preferencesGame = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCES_GAME, Context.MODE_PRIVATE)
        editorGame = preferencesGame.edit()

        editorGame.putString(Constants.GAME_CHAT_VALUE, "message")
        editorGame.apply()

        val sendMessage = root.findViewById<ImageButton>(R.id.send_message)
        val messageList = root.findViewById<ListView>(R.id.messages_list)
        val editText = root.findViewById<EditText>(R.id.editText)
        val guessBtn = root.findViewById<TextView>(R.id.guess)
        val messageBtn = root.findViewById<TextView>(R.id.message_btn)

        val messageAdapter = MessagingAdapter(requireContext())
        //val participantAdapter = ChannelParticipantAdapter(requireContext())
        val factory = InjectorUtils.provideChatViewModelFactory()

        val chatViewModel = ViewModelProvider(this, factory).get(ChatViewModel::class.java)

        messageList.adapter = messageAdapter
        //participantListElement.adapter = participantAdapter

        val typedValue = TypedValue()
        val theme = requireContext().theme
        theme.resolveAttribute(R.attr.colorSecondary, typedValue, true)
        @ColorInt val color = typedValue.data
        messageBtn.setOnClickListener {
            editText.setHintTextColor(color)
            editText.hint = resources.getString(R.string.chat_message_hint)
            editorGame.putString(Constants.GAME_CHAT_VALUE, "message")
            editorGame.apply()
        }
        guessBtn.setOnClickListener {
            editText.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.yellow))
            editText.hint = resources.getString(R.string.chat_guess_hint)
            editorGame.putString(Constants.GAME_CHAT_VALUE, "guess")
            editorGame.apply()
        }

        sendMessage.setOnClickListener {
            val message = editText.text.toString()

            val valueChat = preferencesGame.getString(Constants.GAME_CHAT_VALUE,"message")
            if(valueChat == "message") {
                if (message.isNotBlank()) {
                    chatViewModel.sendMessageToCurrentChannel(message)
                    editText.text.clear() //clear text line

                }
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
        //val mainChannelParticipantList = chatViewModel.getMainChannelParticipants()
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
//        mainChannelParticipantList.observe(viewLifecycleOwner, Observer { participantList->
//            if(!participantList.isNullOrEmpty()){
//                participantAdapter.clear()
//                participantList.forEach{
//                    participantAdapter.add(it)
//                }
//
//            }
//
//        })


        return root
    }


}