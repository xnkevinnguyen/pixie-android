package com.pixie.android.ui.draw.gameInformation

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.pixie.android.R
import com.pixie.android.type.GameMode
import com.pixie.android.type.GameStatus
import com.pixie.android.ui.chat.ChatViewModel
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size


class GameInformationFragment : Fragment() {

    private lateinit var preferencesUser: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.game_information_fragment, container, false)
        val factory = InjectorUtils.provideGameInformationViewModelFactory()
        preferencesUser = requireContext().getSharedPreferences(
            Constants.SHARED_PREFERENCES_LOGIN,
            Context.MODE_PRIVATE
        )

        val gameInfoViewModel =
            ViewModelProvider(this, factory).get(GameInformationViewModel::class.java)

        val chatFactory = InjectorUtils.provideChatViewModelFactory()
        val chatViewModel = ViewModelProvider(this, chatFactory).get(ChatViewModel::class.java)

        val timerElement = root.findViewById<TextView>(R.id.time_left)
        val askHintButtonElement = root.findViewById<Button>(R.id.request_hints)
        val mode = root.findViewById<TextView>(R.id.mode_of_game)
        val round = root.findViewById<TextView>(R.id.round_number)
        val listPlayer = root.findViewById<ListView>(R.id.players_in_game)
        val leaveGameBtn = root.findViewById<Button>(R.id.leave_game)

        val gameID = gameInfoViewModel.getGameSession().value?.id
        val channelID = gameInfoViewModel.getGameSession().value?.channelID


        leaveGameBtn.setOnClickListener {
            gameInfoViewModel.leaveGame()

            if (channelID != null) {
                chatViewModel.exitChannel(channelID)
            }
            if (gameID != null) {
                chatViewModel.exitGame(gameID)
            }

            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_game_selection)
        }
        val gameSessionMode = gameInfoViewModel.getGameSession().value?.mode
        if (gameSessionMode !=null && gameInfoViewModel.shouldDisplayHints(
                gameInfoViewModel.getGameSession().value?.currentDrawerId,
                gameSessionMode
            )
        ) {
            askHintButtonElement.visibility = View.VISIBLE
            val hintsLeft = gameInfoViewModel.getGameSession().value?.hintsLeft
            val hintsLeftElement =
                resources.getString(R.string.ask_hint) + "  |  " + hintsLeft?.toString() + " " + resources.getString(
                    R.string.hints_left
                )
            askHintButtonElement.text = hintsLeftElement
            if (hintsLeft != null && hintsLeft > 0) {
                askHintButtonElement.isEnabled = true
                askHintButtonElement.alpha = 1f
            } else {
                askHintButtonElement.isEnabled = false
                askHintButtonElement.alpha = 0.5f
            }
        } else {
            askHintButtonElement.visibility = View.GONE
        }
        askHintButtonElement.setOnClickListener {
            gameInfoViewModel.askHint()
        }
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    gameInfoViewModel.leaveGame()

                    if (channelID != null) {
                        chatViewModel.exitChannel(channelID)
                    }
                    if (gameID != null) {
                        chatViewModel.exitGame(gameID)
                    }

                    val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.nav_game_selection)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        val listPlayerAdapter = ListPlayerAdapter(requireContext())

        listPlayer.adapter = listPlayerAdapter


        gameInfoViewModel.getTimer().observe(viewLifecycleOwner, Observer {
            timerElement.text = it.toString()
        })

        val preferencesSettings = requireContext().getSharedPreferences(
            Constants.SHARED_PREFERENCES_SETTING,
            Context.MODE_PRIVATE
        )
        val soundOn: Boolean = preferencesSettings.getBoolean(Constants.NOTIFICATION, true)
        val mediaPlayer = chatViewModel.createMediaPlayer(R.raw.end_game, requireContext())

        gameInfoViewModel.getGameSession().observe(viewLifecycleOwner, Observer {
            var roundString: String =
                (it.currentRound.toInt() + 1).toString()
            if (it.mode == GameMode.FREEFORALL) {
                roundString = (it.currentRound.toInt() + 1).toString() + "/3"
            }

            round.text = roundString

            if (gameInfoViewModel.shouldDisplayHints(it.currentDrawerId, it.mode)) {
                askHintButtonElement.visibility = View.VISIBLE
                val hintsLeft = it.hintsLeft
                val hintsLeftElement =
                    resources.getString(R.string.ask_hint) + "  |  " + hintsLeft?.toString() + " " + resources.getString(
                        R.string.hints_left
                    )
                askHintButtonElement.text = hintsLeftElement
                if (hintsLeft != null && hintsLeft > 0) {
                    askHintButtonElement.isEnabled = true
                    askHintButtonElement.alpha = 1f
                } else {
                    askHintButtonElement.isEnabled = false
                    askHintButtonElement.alpha = 0.5f
                }
            } else {
                askHintButtonElement.visibility = View.GONE
            }
            listPlayerAdapter.set(it.players)
            listPlayerAdapter.setDrawer(it.currentDrawerId)


        })


        mode.text = gameInfoViewModel.getGameSession().value?.mode.toString()

        return root
    }


}