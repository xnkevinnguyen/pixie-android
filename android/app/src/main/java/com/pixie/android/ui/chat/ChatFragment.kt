package com.pixie.android.ui.chat

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.type.GameDifficulty
import com.pixie.android.type.GameMode
import com.pixie.android.utilities.InjectorUtils
import org.w3c.dom.Text


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

        val loadHistoryButton = root.findViewById<Button>(R.id.load_chat_history)
        loadHistoryButton.setOnClickListener{
            chatViewModel.getChatHistoryCurrentChannel()
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

        val currentChannelID = chatViewModel.getCurrentChannelID()
        currentChannelID.observe(viewLifecycleOwner, Observer {id->
            // on channel change
            //clear adapter messages
            messageAdapter.clear()
            // load new channel messages
            val messageObject = chatViewModel.getCurrentChannelMessageObject(id)
            messageAdapter.set(messageObject.messageList)
            if(messageObject.isHistoryLoaded){
                loadHistoryButton.visibility = View.INVISIBLE
            }else{
                loadHistoryButton.visibility = View.VISIBLE
            }
        })

        val channelMessages = chatViewModel.getChannelMessageList()
        channelMessages.observe(viewLifecycleOwner, Observer {channelMessagesMap->
            if (!channelMessagesMap.isNullOrEmpty()){
                val messageObject = channelMessagesMap[chatViewModel.getCurrentChannelID().value]

                    // Repopulating the adapter
                if(messageObject !=null) {
                    messageAdapter.set(messageObject.messageList)
                    if(messageObject.isHistoryLoaded){
                        loadHistoryButton.visibility = View.INVISIBLE
                    }else{
                        loadHistoryButton.visibility = View.VISIBLE
                    }
                }


            }

        })
        chatViewModel.getGameInvitation().observe(viewLifecycleOwner, Observer {gameInvitation->
            if(!chatViewModel.getHasGameInvitationBeenShown()){
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.game_invitation)
                val acceptButtonElement = dialog.findViewById<Button>(R.id.accept_invitation)
                val declineButtonElement = dialog.findViewById<Button>(R.id.decline_invitation)

                acceptButtonElement.setOnClickListener{
                    chatViewModel.acceptInvitation(gameInvitation.gameID)
                    dialog.hide()
                }
                declineButtonElement.setOnClickListener {
                    dialog.hide()
                }
                val invitationText = dialog.findViewById<TextView>(R.id.invite_title)
                invitationText.text = String.format(resources.getString(R.string.game_invitation_title),gameInvitation.sender.username)

                val mode = dialog.findViewById<TextView>(R.id.mode_title)
                val img = dialog.findViewById<ImageView>(R.id.mode_picture)
                mode.text = gameInvitation.gameMode.toString()

                val languageElement  = dialog.findViewById<ImageView>(R.id.language_icon)

                if(gameInvitation.language.rawValue == "ENGLISH") {
                    languageElement.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ic_uk_flag) })
                } else{
                    languageElement.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ic_flag_of_france) })
                }
                val difficultyElement = dialog.findViewById<ImageView>(R.id.difficulty_icon)
                if(gameInvitation.difficulty == GameDifficulty.EASY) {
                    difficultyElement.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ic_easy_diff) })
                    difficultyElement.setColorFilter(Color.GREEN)
                } else if(gameInvitation.difficulty == GameDifficulty.MEDIUM){
                    difficultyElement.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ic_medium_diff) })
                    difficultyElement.setColorFilter(Color.YELLOW)
                }else{
                    difficultyElement.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ic_hard_diff) })
                    difficultyElement.setColorFilter(Color.RED)
                }

                if(gameInvitation.gameMode == GameMode.SOLO)
                    img.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_solo_mode))
                else if(gameInvitation.gameMode == GameMode.COOP)
                    img.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_coop_mode))
                else
                    img.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_free_mode))


                dialog.show()
                chatViewModel.confirmInvitationBeenShown()
            }

        })
        chatViewModel.subscribeToGameInvitation()






        return root
    }

}


