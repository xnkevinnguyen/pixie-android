package com.pixie.android.ui.draw.history.connectionHistory

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.pixie.android.R


class ConnectionHistoryFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = Dialog(requireContext())
        val inflater = requireActivity().layoutInflater
        val layout: View = inflater.inflate(R.layout.connection_history_fragment, null)
        builder.setContentView(layout)
        return builder
    }
}