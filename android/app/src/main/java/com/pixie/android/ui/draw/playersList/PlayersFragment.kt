package com.pixie.android.ui.draw.channelList

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
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
import com.pixie.android.ui.draw.playersList.FriendAdapter
import com.pixie.android.ui.draw.profile.ProfileViewModel
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils
import kotlin.random.Random


class PlayersFragment : Fragment() {

    private lateinit var playersViewModel: PlayersViewModel
    private lateinit var preferencesLogin: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val factory = InjectorUtils.providePlayersViewModelFactory()
        playersViewModel = ViewModelProvider(this, factory).get(PlayersViewModel::class.java)
        val root = inflater.inflate(R.layout.player_list_fragment, container, false)
        preferencesLogin = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCES_LOGIN, Context.MODE_PRIVATE)

        val search = root.findViewById<EditText>(R.id.search_player)

        //Set Me
        val myUsername = root.findViewById<TextView>(R.id.me_username)
        val name = preferencesLogin.getString(Constants.USERNAME, null)
        myUsername.text = name

        val myBadge = root.findViewById<ImageView>(R.id.me_online_badge)
        val myAvatar = root.findViewById<ImageView>(R.id.me_avatar)
        val me = playersViewModel.getMe()
        if(me!=null) {
            setColor(myBadge, myAvatar, me)
        }

        val friendSection = root.findViewById<LinearLayout>(R.id.friends)
        val otherSection = root.findViewById<LinearLayout>(R.id.others)
        val participantListElement = root.findViewById<ListView>(R.id.participants)
        val participantAdapter = ChannelParticipantAdapter(requireContext())
        participantListElement.adapter = participantAdapter

        val friendListElement = root.findViewById<ListView>(R.id.friend_participants)
        val friendAdapter = FriendAdapter(requireContext())
        friendListElement.adapter = friendAdapter

        val currentChannelID = playersViewModel.getCurrentChannelID()
        playersViewModel.fetchFriendList()

        currentChannelID.observe(viewLifecycleOwner, Observer {id->
            // on channel change
            val participantList = playersViewModel.getCurrentChannelParticipants(id)
            val friendList = playersViewModel.getFriendList().value

            var otherPlayersList = arrayListOf<ChannelParticipant>()
            var listOfFriends = arrayListOf<ChannelParticipant>()
            if (friendList != null) {
                otherPlayersList = getListOfOtherPlayers(participantList, friendList)
                listOfFriends = getListOfFriends(participantList, friendList)

            }
            friendAdapter.set(listOfFriends)
            participantAdapter.set(otherPlayersList)

            checkIfFriendSectionShouldBeVisible(otherPlayersList, otherSection, listOfFriends, friendSection)
        })


        val userChannels= playersViewModel.getUserChannels()
        userChannels.observe(viewLifecycleOwner, Observer {
            val participantList = playersViewModel.getCurrentChannelParticipants()
            val friendList = playersViewModel.getFriendList().value

            var otherPlayersList = arrayListOf<ChannelParticipant>()
            var listOfFriends = arrayListOf<ChannelParticipant>()

            if (friendList != null) {
                otherPlayersList = getListOfOtherPlayers(participantList, friendList)
                listOfFriends = getListOfFriends(participantList, friendList)
            }
            friendAdapter.set(listOfFriends)
            participantAdapter.set(otherPlayersList)

            checkIfFriendSectionShouldBeVisible(otherPlayersList, otherSection, listOfFriends, friendSection)
        })

        // update list on new friend as well
        playersViewModel.getFriendList().observe(viewLifecycleOwner, Observer {
            val participantList = playersViewModel.getCurrentChannelParticipants()

            val listOfFriends = getListOfFriends(participantList, it)
            val otherPlayersList = getListOfOtherPlayers(participantList, it)

            friendAdapter.set(listOfFriends)
            participantAdapter.set(otherPlayersList)

            checkIfFriendSectionShouldBeVisible(otherPlayersList, otherSection, listOfFriends, friendSection)
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

                    follow.text = resources.getString(R.string.follow)

                    follow.setOnClickListener {
                        playersViewModel.addFriend(user) {
                            if (it.isSuccess) {
                                Toast.makeText(
                                    requireContext(),
                                    requireContext().resources.getString(R.string.success),
                                    Toast.LENGTH_LONG
                                ).show()
                                follow.text = resources.getString(R.string.unfollow)
                                dialog.dismiss()
                            } else if (!it.isSuccess) {
                                Toast.makeText(
                                    requireContext(),
                                    requireContext().resources.getString(R.string.error),
                                    Toast.LENGTH_LONG
                                ).show()
                                follow.text = resources.getString(R.string.follow)

                            }
                        }
                    }


                    val invite = dialog.findViewById<Button>(R.id.invite)
                    if (playersViewModel.getGameSession().value?.id == null) {
                        invite.visibility = View.GONE
                    } else {
                        invite.visibility = View.VISIBLE
                    }

                    invite.setOnClickListener {
                        invite.isEnabled = false
                        playersViewModel.sendGameInvitation(user.id) {
                            if (it.isSuccess) {
                                Toast.makeText(
                                    context,
                                    resources.getString(R.string.success),
                                    Toast.LENGTH_LONG
                                ).show()
                                dialog.dismiss()
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

                    val onlineIconElement = dialog.findViewById<ImageView>(R.id.online_badge)
                    val avatarElement = dialog.findViewById<ImageView>(R.id.avatar_participant)
                    setColor(onlineIconElement, avatarElement, user)


                    dialog.show()

                }
            }

        friendListElement.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val user: ChannelParticipant =
                    friendListElement.getItemAtPosition(position) as ChannelParticipant
                if (!user.isVirtual!!) {
                    val dialog = Dialog(requireContext())
                    dialog.setContentView(R.layout.other_user_info)
                    dialog.findViewById<TextView>(R.id.user_name).text = user.username
                    val follow = dialog.findViewById<Button>(R.id.follow)

                    follow.text = resources.getString(R.string.unfollow)
                    follow.setOnClickListener {
                        playersViewModel.removeFriend(user.id) {
                            if (it.isSuccess) {
                                Toast.makeText(
                                    requireContext(),
                                    requireContext().resources.getString(R.string.success),
                                    Toast.LENGTH_LONG
                                ).show()
                                follow.text = resources.getString(R.string.follow)
                                dialog.dismiss()
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

                    val invite = dialog.findViewById<Button>(R.id.invite)
                    if (playersViewModel.getGameSession().value?.id == null) {
                        invite.visibility = View.GONE
                    } else {
                        invite.visibility = View.VISIBLE
                    }

                    invite.setOnClickListener {
                        invite.isEnabled = false
                        playersViewModel.sendGameInvitation(user.id) {
                            if (it.isSuccess) {
                                Toast.makeText(
                                    context,
                                    resources.getString(R.string.success),
                                    Toast.LENGTH_LONG
                                ).show()
                                dialog.dismiss()
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

                    val onlineIconElement = dialog.findViewById<ImageView>(R.id.online_badge)
                    val avatarElement = dialog.findViewById<ImageView>(R.id.avatar_participant)
                    setColor(onlineIconElement, avatarElement, user)

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
                friendAdapter.filter.filter(s.toString())
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

    private fun isUser(user: ChannelParticipant): Boolean{
        val preferences = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCES_LOGIN, Context.MODE_PRIVATE)
        val userIDPreference = preferences.getString(Constants.USER_ID, null)
        if(userIDPreference.toDouble() == user.id){
            return true
        }
        return false
    }

    private fun setColor(onlineIconElement:ImageView, avatarElement:ImageView,
                         participant:ChannelParticipant){

        if (participant.isOnline == false) {
            onlineIconElement.setColorFilter(Color.GRAY)
        }

        var foregroundColor: Int? = null
        if (!participant.avatarForeground.isNullOrEmpty()) {
            foregroundColor = Color.parseColor(participant.avatarForeground)
        }
        if (foregroundColor == null) {
            foregroundColor = Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
        }
        avatarElement.setColorFilter(
            foregroundColor
        )

        var backgroundColor: Int? = null
        if (!participant.avatarBackground.isNullOrEmpty()) {
            backgroundColor = Color.parseColor(participant.avatarBackground)
        }
        if (backgroundColor == null) {
            backgroundColor = Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
        }
        avatarElement.backgroundTintList = ColorStateList.valueOf(
            backgroundColor
        )
    }

    private fun getListOfOtherPlayers(participantList:ArrayList<ChannelParticipant>,
                                      friendList:ArrayList<ChannelParticipant>): ArrayList<ChannelParticipant>{
        val otherPlayerList= arrayListOf<ChannelParticipant>()
        for (participant in participantList){
            if(!friendList.contains(participant) && !isUser(participant)){
                otherPlayerList.add(participant)
            }
        }
        return otherPlayerList
    }

    private fun getListOfFriends(participantList:ArrayList<ChannelParticipant>,
                                      friendList:ArrayList<ChannelParticipant>): ArrayList<ChannelParticipant>{
        val listOfFriends= arrayListOf<ChannelParticipant>()
        for (participant in participantList){
            if(friendList.contains(participant) && !isUser(participant)){
                listOfFriends.add(participant)
            }
        }
        return listOfFriends
    }

    private fun checkIfFriendSectionShouldBeVisible(participantList:ArrayList<ChannelParticipant>, otherPlayerSection:LinearLayout,
                                                    friendList:ArrayList<ChannelParticipant>, friendSection:LinearLayout){
        if(friendList.size == 0){
            friendSection.visibility = View.GONE
        } else {
            friendSection.visibility = View.VISIBLE
        }

        if(participantList.size == 0 ){
            otherPlayerSection.visibility = View.GONE
        }else{
            otherPlayerSection.visibility = View.VISIBLE
        }
    }

}