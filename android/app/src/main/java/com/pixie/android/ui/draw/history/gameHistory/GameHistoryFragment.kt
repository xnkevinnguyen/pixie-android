package com.pixie.android.ui.draw.history.gameHistory

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.model.history.GameHistory
import com.pixie.android.ui.draw.history.connectionHistory.ConnectionAdapter
import com.pixie.android.ui.draw.profile.ProfileViewModel
import com.pixie.android.utilities.InjectorUtils


class GameHistoryFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = Dialog(requireContext())
        val inflater = requireActivity().layoutInflater
        val layout: View = inflater.inflate(R.layout.game_history_fragment, null)
        builder.setContentView(layout)

        val closeBtn = builder.findViewById<ImageView>(R.id.close)
        closeBtn.setOnClickListener {
            builder.dismiss()
        }

        val width = 1500
        val height = 1050
        builder.window.setLayout(width, height)

        val listOfGames = builder.findViewById<ListView>(R.id.history_game_list)
        val gameAdapter = GameAdapter(requireContext())
        listOfGames.adapter = gameAdapter

        val factory = InjectorUtils.provideProfileViewModelFactory()
        val profileViewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)

        profileViewModel.fetchAllInfo()
        val gameList = profileViewModel.getGameList()

        if(gameList.value !=null){
            gameAdapter.set(gameList.value)
        }

        profileViewModel.getGameList().observe(requireActivity(), Observer { gameList->
            gameAdapter.set(gameList)
        })

        return builder
    }
}