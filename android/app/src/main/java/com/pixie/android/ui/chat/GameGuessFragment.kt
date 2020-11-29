package com.pixie.android.ui.chat

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Layout
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.pixie.android.R
import com.pixie.android.ui.draw.gameInformation.GameInformationViewModel
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils


class GameGuessFragment : Fragment() {
    private lateinit var preferencesGame: SharedPreferences
    private lateinit var editorGame: SharedPreferences.Editor
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(
            R.layout.game_guess_fragment,
            container, false
        )

        preferencesGame = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCES_GAME, Context.MODE_PRIVATE)
        editorGame = preferencesGame.edit()

        editorGame.putString(Constants.GAME_CHAT_VALUE, "message")
        editorGame.apply()


        val sendMessage = root.findViewById<ImageButton>(R.id.send_message)

        val factory = InjectorUtils.provideGameChatViewModelFactory()

        val gameChatViewModel = ViewModelProvider(this, factory).get(GameChatViewModel::class.java)

        val typedValue = TypedValue()
        val theme = requireContext().theme
        theme.resolveAttribute(R.attr.colorSecondary, typedValue, true)
        @ColorInt val color = typedValue.data


        val factoryChat = InjectorUtils.provideChatViewModelFactory()
        val chatViewModel = ViewModelProvider(ViewModelStore(),factoryChat).get(ChatViewModel::class.java)
        val preferencesSettings = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCES_SETTING, Context.MODE_PRIVATE)
        val soundOn:Boolean = preferencesSettings.getBoolean(Constants.NOTIFICATION, true)
        val mediaPlayerCorrectAnswer = chatViewModel.createMediaPlayer(R.raw.correct, requireContext())
        val mediaPlayerIncorrectAnswer = chatViewModel.createMediaPlayer(R.raw.incorrect, requireContext())
        val gameInfoFactory = InjectorUtils.provideGameInformationViewModelFactory()

        val gameInfoViewModel =
            ViewModelProvider(this, gameInfoFactory).get(GameInformationViewModel::class.java)
        val guessEditText = root.findViewById<EditText>(R.id.edit_guess)
        val guessArea = root.findViewById<View>(R.id.guess_area)

        val guessesLeft = gameInfoViewModel.getGameSession().value?.guessesLeft?.toInt()

        guessEditText.hint = String.format(resources.getString(R.string.guess_left),guessesLeft)
        sendMessage.setOnClickListener {
            val message = guessEditText.text.toString()
            if (message.isNotBlank()) {
                gameChatViewModel.sendGuess(message) {
                    if (it == true) {
                        Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.correct_guess),
                            Toast.LENGTH_LONG
                        ).show()
                        if (soundOn) chatViewModel.startMediaPlayer(mediaPlayerCorrectAnswer)
                        else chatViewModel.releaseMediaPlayer(mediaPlayerCorrectAnswer)
                    } else if (it == false) {
                        Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.incorrect_guess),
                            Toast.LENGTH_LONG
                        ).show()
                        if (soundOn) chatViewModel.startMediaPlayer(mediaPlayerIncorrectAnswer)
                        else chatViewModel.releaseMediaPlayer(mediaPlayerIncorrectAnswer)
                    }
                }
            }
            guessEditText.text.clear()

        }
        gameInfoViewModel.getGameSession().observe(viewLifecycleOwner, Observer {
            if(it.guessesLeft !=null){
                guessEditText.hint = String.format(resources.getString(R.string.guess_left),it.guessesLeft?.toInt())

            }
            if(gameInfoViewModel.shouldDisplayHints(it.currentDrawerId,it.mode)){
                guessArea.visibility = View.VISIBLE
            }else{
                guessArea.visibility = View.INVISIBLE
            }
        })

        // Enter button on real keyboard if attached to android
        guessEditText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER) {
                    val message = guessEditText.text.toString()
                    if (message.isNotBlank()) {
                        gameChatViewModel.sendGuess(message){
                            if(it ==true){
                                Toast.makeText(requireContext(),
                                    resources.getString(R.string.correct_guess),
                                    Toast.LENGTH_LONG).show()
                                if (soundOn) chatViewModel.startMediaPlayer(mediaPlayerCorrectAnswer)
                                else chatViewModel.releaseMediaPlayer(mediaPlayerCorrectAnswer)
                            }else if( it==false){
                                Toast.makeText(requireContext(),
                                    resources.getString(R.string.incorrect_guess),
                                    Toast.LENGTH_LONG).show()
                                if(soundOn)chatViewModel.startMediaPlayer(mediaPlayerIncorrectAnswer)
                                else chatViewModel.releaseMediaPlayer(mediaPlayerIncorrectAnswer)
                            }
                        }
                        guessEditText.text.clear() //clear text line
                    }
                    return true
                }
                return false
            }
        })

        // Enter button on soft keyboard
        guessEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val message = guessEditText.text.toString()

                if (message.isNotBlank()) {
                    gameChatViewModel.sendGuess(message){
                        if(it ==true){
                            Toast.makeText(requireContext(),
                                resources.getString(R.string.correct_guess),
                                Toast.LENGTH_LONG).show()
                            if(soundOn)chatViewModel.startMediaPlayer(mediaPlayerCorrectAnswer)
                            else chatViewModel.releaseMediaPlayer(mediaPlayerCorrectAnswer)
                        }else if( it==false){
                            Toast.makeText(requireContext(),
                                resources.getString(R.string.incorrect_guess),
                                Toast.LENGTH_LONG).show()
                            if(soundOn)chatViewModel.startMediaPlayer(mediaPlayerIncorrectAnswer)
                            else chatViewModel.releaseMediaPlayer(mediaPlayerIncorrectAnswer)
                        }
                    }
                    guessEditText.text.clear()
                }
                true
            } else {
                false
            }
        }

        return root
    }


}