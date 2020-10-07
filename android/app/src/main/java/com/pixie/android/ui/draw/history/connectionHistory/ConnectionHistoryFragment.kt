package com.pixie.android.ui.draw.history.connectionHistory

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import com.pixie.android.R
import com.pixie.android.ui.chat.MessagingAdapter


class ConnectionHistoryFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = Dialog(requireContext())
        val inflater = requireActivity().layoutInflater
        val layout: View = inflater.inflate(R.layout.connection_history_fragment, null)
        builder.setContentView(layout)

        val connectionList = builder.findViewById<ListView>(R.id.history_conn_disc_list)
        val connectionAdapter = ConnectionAdapter(requireContext())
        connectionList.adapter = connectionAdapter

        return builder
    }
}