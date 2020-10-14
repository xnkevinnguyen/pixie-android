package com.pixie.android.ui.draw.channelList

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.ui.chat.ChannelParticipantAdapter
import com.pixie.android.ui.chat.ChatViewModel
import com.pixie.android.utilities.InjectorUtils


class PlayersFragment : Fragment() {

    private lateinit var playersViewModel: PlayersViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val factory = InjectorUtils.providePlayersViewModelFactory()
        playersViewModel = ViewModelProvider(this, factory).get(PlayersViewModel::class.java)
        val root = inflater.inflate(R.layout.player_list_fragment, container, false)

        val search = root.findViewById<EditText>(R.id.search_player)
        val participantListElement = root.findViewById<ListView>(R.id.participants)
        val participantAdapter = ChannelParticipantAdapter(requireContext())
        participantListElement.adapter = participantAdapter

        //TODO should be removed and replaced with players from current channel
        val mainChannelParticipantList = playersViewModel.getMainChannelParticipants()
        mainChannelParticipantList.observe(viewLifecycleOwner, Observer { participantList ->
            if (!participantList.isNullOrEmpty()) {
                participantAdapter.clear()
                participantList.forEach {
                    participantAdapter.add(it)
                }

            }

        })
        val currentChannelID = playersViewModel.getCurrentChannelID()

        currentChannelID.observe(viewLifecycleOwner, Observer {id->
            // on channel change

            // load new channel messages
            val participantList = playersViewModel.getCurrentChannelParticipants(id)
            participantAdapter.set(participantList)

        })

        val userChannels= playersViewModel.getUserChannels()
        userChannels.observe(viewLifecycleOwner, Observer {
            val participantList = playersViewModel.getCurrentChannelParticipants()
            participantAdapter.set(participantList)

        })
        participantListElement.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, childView, position, id ->
                val user: ChannelParticipant =
                    participantListElement.getItemAtPosition(position) as ChannelParticipant
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.other_user_info)
                dialog.findViewById<TextView>(R.id.user_name).text = user.username
                val follow = dialog.findViewById<Button>(R.id.follow)
                follow.setOnClickListener {
                    if (follow.text == "Follow") {
                        follow.text = resources.getString(R.string.unfollow)
                    } else follow.text = resources.getString(R.string.follow)
                }

                dialog.show()
            }

        search.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                println("Text [$s]")
                participantAdapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {}
        })
        return root
    }


}