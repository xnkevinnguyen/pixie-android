package com.pixie.android.ui.draw.ChannelList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.ui.chat.ChannelParticipantAdapter
import com.pixie.android.ui.draw.home.HomeViewModel
import com.pixie.android.utilities.InjectorUtils

class PlayersFragment: Fragment() {

    private lateinit var playersViewModel: PlayersViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val factory = InjectorUtils.providePlayersViewModelFactory()
        playersViewModel = ViewModelProvider(this, factory).get(PlayersViewModel::class.java)

        val root = inflater.inflate(R.layout.player_list_fragment, container, false)

        val participantListElement = root.findViewById<ListView>(R.id.participants)
        val participantAdapter = ChannelParticipantAdapter(requireContext())
        participantListElement.adapter = participantAdapter

        val mainChannelParticipantList = playersViewModel.getMainChannelParticipants()
        mainChannelParticipantList.observe(viewLifecycleOwner, Observer { participantList->
            if(!participantList.isNullOrEmpty()){
                participantAdapter.clear()
                participantList.forEach{
                    participantAdapter.add(it)
                }

            }

        })
        return root
    }
}