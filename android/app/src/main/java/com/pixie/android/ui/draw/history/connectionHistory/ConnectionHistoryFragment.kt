package com.pixie.android.ui.draw.history.connectionHistory

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.pixie.android.R
import com.pixie.android.ui.chat.MessagingAdapter
import com.pixie.android.ui.draw.profile.ProfileViewModel
import com.pixie.android.utilities.InjectorUtils


class ConnectionHistoryFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = Dialog(requireContext())
        val inflater = requireActivity().layoutInflater
        val layout: View = inflater.inflate(R.layout.connection_history_fragment, null)
        builder.setContentView(layout)

        val closeBtn = builder.findViewById<ImageView>(R.id.close)
        closeBtn.setOnClickListener {
            builder.dismiss()
        }

        val connectionList = builder.findViewById<ListView>(R.id.history_conn_disc_list)
        val connectionAdapter = ConnectionAdapter(requireContext())
        connectionList.adapter = connectionAdapter

        val factory = InjectorUtils.provideProfileViewModelFactory()
        val profileViewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)

        profileViewModel.fetchAllInfo()
        val loglist = profileViewModel.getLogList()

        if(loglist.value !=null){
            connectionAdapter.set(loglist.value)
        }

        profileViewModel.getLogList().observe(requireActivity(), Observer { logList->
            connectionAdapter.set(logList)
        })

        return builder
    }

}