package com.pixie.android.ui.chat

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.type.GameMode
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


        val messageAdapter = MessagingAdapter(requireContext())
        //val participantAdapter = ChannelParticipantAdapter(requireContext())
        val factory = InjectorUtils.provideGameChatViewModelFactory()

        val gameChatViewModel = ViewModelProvider(this, factory).get(GameChatViewModel::class.java)

        messageList.adapter = messageAdapter
        //participantListElement.adapter = participantAdapter

        val typedValue = TypedValue()
        val theme = requireContext().theme
        theme.resolveAttribute(R.attr.colorSecondary, typedValue, true)


        val channelMessages = gameChatViewModel.getChannelMessageList()
        val gameMode = gameChatViewModel.getGameSession().value?.mode
        channelMessages.observe(viewLifecycleOwner, Observer {channelMessagesMap->

            if (!channelMessagesMap.isNullOrEmpty()){
                val messageObject = channelMessagesMap[gameChatViewModel.getGameChannelID()]

                // Repopulating the adapter
                if(messageObject !=null) {
                    messageAdapter.set(ArrayList(messageObject.messageList.filter {
                        !(it.shouldBeHidden && gameMode ==GameMode.FREEFORALL)
                    }))

                }


            }

        })

        sendMessage.setOnClickListener {
            val message = editText.text.toString()

            gameChatViewModel.sendMessage(message)
            editText.text.clear()

        }

        // Enter button on real keyboard if attached to android
        editText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER) {
                    val message = editText.text.toString()
                    if (message.isNotBlank()) {
                        gameChatViewModel.sendMessage(message)
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
                    gameChatViewModel.sendMessageToGameChannel(message)
                    editText.text.clear()
                }
                true
            } else {
                false
            }
        }


        return root
    }


}