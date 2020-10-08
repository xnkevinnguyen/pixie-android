package com.pixie.android.ui.draw.history.gameHistory

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import com.pixie.android.R
import com.pixie.android.ui.draw.history.connectionHistory.ConnectionAdapter


class GameHistoryFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = Dialog(requireContext())
        val inflater = requireActivity().layoutInflater
        val layout: View = inflater.inflate(R.layout.game_history_fragment, null)
        builder.setContentView(layout)

        val gameList = builder.findViewById<ListView>(R.id.history_game_list)
        val gameAdapter = ConnectionAdapter(requireContext())
        gameList.adapter = gameAdapter

        return builder
    }
}