package com.pixie.android.ui.draw.history.gameHistory

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.pixie.android.R


class GameHistoryFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = Dialog(requireContext())
        val inflater = requireActivity().layoutInflater
        val layout: View = inflater.inflate(R.layout.game_history_fragment, null)
        builder.setContentView(layout)
        return builder
    }
}