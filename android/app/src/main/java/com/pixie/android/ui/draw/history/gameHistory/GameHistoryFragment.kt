package com.pixie.android.ui.draw.history.gameHistory

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import com.pixie.android.R
import com.pixie.android.model.history.GameHistory
import com.pixie.android.ui.draw.history.connectionHistory.ConnectionAdapter


class GameHistoryFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = Dialog(requireContext())
        val inflater = requireActivity().layoutInflater
        val layout: View = inflater.inflate(R.layout.game_history_fragment, null)
        builder.setContentView(layout)

        val width = 1500
        val height = 1050
        builder.window.setLayout(width, height)

        val gameList = builder.findViewById<ListView>(R.id.history_game_list)
        val gameAdapter = GameAdapter(requireContext())
        gameList.adapter = gameAdapter

        val game : GameHistory = GameHistory(date = "this", time="time", points = "200", winner = "joe", score = "300", difficulty = "easy", gameMode = "free")
        gameAdapter.add(game)

        return builder
    }
}