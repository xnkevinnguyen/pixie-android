package com.pixie.android.ui.draw.gameInformation

import android.app.ActionBar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.pixie.android.R
import com.pixie.android.type.GameStatus
import com.pixie.android.ui.chat.ChatViewModel
import com.pixie.android.utilities.InjectorUtils


class GameInformationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.game_information_fragment, container, false)
        val factory = InjectorUtils.provideGameInformationViewModelFactory()
        val gameInfoViewModel =
            ViewModelProvider(this, factory).get(GameInformationViewModel::class.java)

        val chatFactory = InjectorUtils.provideChatViewModelFactory()
        val chatViewModel = ViewModelProvider(this, chatFactory).get(ChatViewModel::class.java)

        val timerElement = root.findViewById<TextView>(R.id.time_left)
        val guessLeftElement = root.findViewById<TextView>(R.id.number_guess_left)
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
            navController.navigate(R.id.nav_home)
        }

        guessLeftElement.text =
            gameInfoViewModel.getGameSession().value?.guessesLeft?.toInt().toString()

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
                    navController.navigate(R.id.nav_home)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        val listPlayerAdapter = ListPlayerAdapter(requireContext())

        listPlayer.adapter = listPlayerAdapter


        gameInfoViewModel.getTimer().observe(viewLifecycleOwner, Observer {
            timerElement.text = it.toString()
        })

        gameInfoViewModel.getGameSession().observe(viewLifecycleOwner, Observer {
            val roundString =
                resources.getString(R.string.round_turn) + (it.currentRound.toInt() + 1)
            round.text = roundString
            guessLeftElement.text = it.guessesLeft?.toInt().toString()
            listPlayerAdapter.set(it.players)
            listPlayerAdapter.setDrawer(it.currentDrawerId)
            if (it.status.equals(GameStatus.ENDED)) {
                //TODO Show UI for end game
            }
        })

//        gameInfoViewModel.getPlayers().observe(viewLifecycleOwner, Observer {
//            listPlayerAdapter.set(it)
//        })

        mode.text = gameInfoViewModel.getGameSession().value?.mode.toString()

        return root
    }



}