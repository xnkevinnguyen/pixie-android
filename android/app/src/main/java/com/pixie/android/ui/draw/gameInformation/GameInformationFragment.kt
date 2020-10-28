package com.pixie.android.ui.draw.gameInformation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.model.game.GamePlayerData
import com.pixie.android.utilities.InjectorUtils


class GameInformationFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root =inflater.inflate(R.layout.game_information_fragment, container, false)
        val timerElement = root.findViewById<TextView>(R.id.time_left)
        val mode = root.findViewById<TextView>(R.id.mode_of_game)
        val round = root.findViewById<TextView>(R.id.round_number)
        val listPlayer = root.findViewById<ListView>(R.id.players_in_game)
        val listPlayerAdapter = ListPlayerAdapter(requireContext())

        listPlayer.adapter = listPlayerAdapter

        val factory = InjectorUtils.provideGameInformationViewModelFactory()
        val gameInfoViewModel = ViewModelProvider(this,factory).get(GameInformationViewModel::class.java)
        gameInfoViewModel.getTimer().observe(viewLifecycleOwner, Observer {
          timerElement.text = it.toString()
        })

        gameInfoViewModel.getGameSession().observe(viewLifecycleOwner, Observer {
            round.text = "Round " + (it.currentRound.toInt() + 1) + " of 3"
            listPlayerAdapter.setDrawer(it.currentDrawerId)
        })

        gameInfoViewModel.getPlayers().observe(viewLifecycleOwner, Observer {
            listPlayerAdapter.set(it)
        })

        mode.text = gameInfoViewModel.getGameSession().value?.mode.toString()

        return root
    }
}