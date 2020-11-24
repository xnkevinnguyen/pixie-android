package com.pixie.android.ui.draw.channelList

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.pixie.android.R
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.ui.chat.ChannelParticipantAdapter
import com.pixie.android.ui.chat.ChatViewModel
import com.pixie.android.utilities.Constants
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

        val currentChannelID = playersViewModel.getCurrentChannelID()

        currentChannelID.observe(viewLifecycleOwner, Observer {id->
            // on channel change
            val participantList = playersViewModel.getCurrentChannelParticipants(id)
            participantAdapter.set(participantList)
        })
        playersViewModel.fetchFriendList()

        val userChannels= playersViewModel.getUserChannels()
        userChannels.observe(viewLifecycleOwner, Observer {
            val participantList = playersViewModel.getCurrentChannelParticipants()
            participantAdapter.set(participantList)
        })
        // update list on new friend as well
        playersViewModel.getFriendList().observe(viewLifecycleOwner, Observer {
            val participantList = playersViewModel.getCurrentChannelParticipants()
            participantAdapter.set(participantList)
        })


        participantListElement.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val user: ChannelParticipant =
                    participantListElement.getItemAtPosition(position) as ChannelParticipant
                if (!user.isVirtual!!) {
                    val dialog = Dialog(requireContext())
                    dialog.setContentView(R.layout.other_user_info)
                    dialog.findViewById<TextView>(R.id.user_name).text = user.username
                    val follow = dialog.findViewById<Button>(R.id.follow)
                    if (isUser(user)) follow.visibility = View.GONE

                    val friendList = playersViewModel.getFriendList().value
                    if (friendList != null) {
                        if (friendList.contains(user)) follow.text =
                            resources.getString(R.string.unfollow)
                        else follow.text = resources.getString(R.string.follow)
                    } else {
                        follow.text = resources.getString(R.string.follow)
                    }

                    follow.setOnClickListener {
                        if (friendList != null) {
                            if (!friendList.contains(user)) {
                                playersViewModel.addFriend(user) {
                                    if (it.isSuccess) {
                                        Toast.makeText(
                                            requireContext(),
                                            requireContext().resources.getString(R.string.success),
                                            Toast.LENGTH_LONG
                                        ).show()
                                        follow.text = resources.getString(R.string.unfollow)
                                    } else if (!it.isSuccess) {
                                        Toast.makeText(
                                            requireContext(),
                                            requireContext().resources.getString(R.string.error),
                                            Toast.LENGTH_LONG
                                        ).show()
                                        follow.text = resources.getString(R.string.follow)

                                    }
                                }
                            } else {
                                playersViewModel.removeFriend(user.id) {
                                    if (it.isSuccess) {
                                        Toast.makeText(
                                            requireContext(),
                                            requireContext().resources.getString(R.string.success),
                                            Toast.LENGTH_LONG
                                        ).show()
                                        follow.text = resources.getString(R.string.follow)
                                    } else if (!it.isSuccess) {
                                        Toast.makeText(
                                            requireContext(),
                                            requireContext().resources.getString(R.string.error),
                                            Toast.LENGTH_LONG
                                        ).show()
                                        follow.text = resources.getString(R.string.unfollow)
                                    }
                                }
                            }
                            val participantList = playersViewModel.getCurrentChannelParticipants()
                            participantAdapter.set(participantList)
                        }
                    }

                    val invite = dialog.findViewById<Button>(R.id.invite)
                    if (playersViewModel.getGameSession().value?.id == null) {
                        invite.visibility = View.GONE
                    } else {
                        invite.visibility = View.VISIBLE
                    }
                    if (isUser(user)) invite.visibility = View.GONE

                    invite.setOnClickListener {
                        invite.isEnabled = false
                        playersViewModel.sendGameInvitation(user.id) {
                            if (it.isSuccess == true) {
                                Toast.makeText(
                                    context,
                                    resources.getString(R.string.success),
                                    Toast.LENGTH_LONG
                                ).show()
                            } else if (!it.isSuccess) {
                                Toast.makeText(
                                    context,
                                    resources.getString(R.string.error),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            invite.isEnabled = true
                        }
                    }

                    dialog.show()

                }
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

    fun isUser(user: ChannelParticipant): Boolean{
        val preferences = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCES_LOGIN, Context.MODE_PRIVATE)
        val userIDPreference = preferences.getString(Constants.USER_ID, null)
        if(userIDPreference.toDouble() == user.id){
            return true
        }
        return false
    }

}