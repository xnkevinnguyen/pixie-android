package com.pixie.android.ui.draw.channelList

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.pixie.android.R
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.type.GameMode
import com.pixie.android.type.GameStatus
import com.pixie.android.ui.chat.ChatViewModel
import com.pixie.android.ui.chat.UserChannelAdapter
import com.pixie.android.ui.draw.gameInformation.GameInformationViewModel
import com.pixie.android.utilities.InjectorUtils


class ChannelFragment : Fragment() {

    private lateinit var channelViewModel: ChannelViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val factory = InjectorUtils.provideChannelViewModelFactory()
        channelViewModel = ViewModelProvider(this, factory).get(ChannelViewModel::class.java)

        val root = inflater.inflate(R.layout.channel_list_fragment, container, false)

        val channelListElement = root.findViewById<ListView>(R.id.channels_list)
        val addBtn = root.findViewById<Button>(R.id.add_channel)
        val startGameBtn = root.findViewById<Button>(R.id.start_game)
        val leaveGameBtn = root.findViewById<Button>(R.id.leave_game)
        val addPlayerBtn = root.findViewById<Button>(R.id.add_players)

        val userChannelAdapter = UserChannelAdapter(requireContext(), requireActivity())
        channelListElement.adapter = userChannelAdapter

        val factoryChat = InjectorUtils.provideChatViewModelFactory()
        val chatViewModel = ViewModelProvider(this, factoryChat).get(ChatViewModel::class.java)
        val userChannels = chatViewModel.getUserChannels()
        val joinChannelAdapter = ChannelJoinAdapter(requireContext())
        val addPlayerAdapter = AddPlayerAdapter(requireContext())

        userChannelAdapter.set(userChannels.value)
        val currentChannelID = chatViewModel.getCurrentChannelID()
        currentChannelID.observe(viewLifecycleOwner, Observer { id ->
            userChannelAdapter.setChannelIdSelected(id)
            val currentChannel = channelViewModel.getCurrentChannelInfo(id)
            if (currentChannel != null) {
                if (currentChannel.gameID != null) {
                    startGameBtn.visibility = View.VISIBLE
                    leaveGameBtn.visibility = View.VISIBLE
                    addPlayerBtn.visibility = View.VISIBLE
                    val nParticipant = currentChannel.participantList?.size
                    if (nParticipant != null && nParticipant >= 2) {

                        startGameBtn.isEnabled = true
                        startGameBtn.alpha = 1f
                    } else {
                        startGameBtn.isEnabled = false
                        startGameBtn.alpha = 0.5f
                    }
                } else {
                    startGameBtn.visibility = View.GONE
                    leaveGameBtn.visibility = View.GONE
                    addPlayerBtn.visibility = View.GONE
                }
            }
        })

        userChannels.observe(viewLifecycleOwner, Observer { userChannelList ->
            userChannelAdapter.set(userChannelList)
            val currentChannelID = chatViewModel.getCurrentChannelID().value
            val currentChannel = channelViewModel.getCurrentChannelInfo(currentChannelID)
            val participants = currentChannel?.participantList

            if (participants !=null && participants.size >= 2) {
                val atLeast2ParticipantReel = areAtLeast2ParticipantReel(participants)
                if(atLeast2ParticipantReel) {
                    startGameBtn.isEnabled = true
                    startGameBtn.alpha = 1f
                }else{
                    startGameBtn.isEnabled = false
                    startGameBtn.alpha = 0.5f
                }
            } else {
                startGameBtn.isEnabled = false
                startGameBtn.alpha = 0.5f
            }
        })

        //start game
        startGameBtn.setOnClickListener {
            val gameID = channelViewModel.getCurrentChannelInfo(currentChannelID.value)?.gameID
            if (gameID != null) {
                chatViewModel.startGameSession(gameID) {
                    if (it.isSuccess) {
                        val navController =
                            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                        navController.navigate(R.id.nav_drawing)
                    }
                }


            }

        }


        leaveGameBtn.setOnClickListener {
            val gameID = channelViewModel.getCurrentChannelInfo(currentChannelID.value)?.gameID
            val channelID =
                channelViewModel.getCurrentChannelInfo(currentChannelID.value)?.channelID
            if (channelID != null) {
                chatViewModel.exitChannel(channelID)
            }
            if (gameID != null) {
                chatViewModel.exitGame(gameID)
            }
        }


        //add players
        addPlayerBtn.setOnClickListener {
            val dialog = Dialog(requireContext())

            val filteredListOfOnlineUser = arrayListOf<ChannelParticipant>()
            val listUsers = channelViewModel.getAllUsers()
            listUsers.observe(viewLifecycleOwner, Observer {
                for (user in it){
                    if(user.isOnline != null) {
                        if (user.isOnline && !filteredListOfOnlineUser.contains(user)){
                            filteredListOfOnlineUser.add(user)
                        }
                    }
                }
                addPlayerAdapter.set(filteredListOfOnlineUser)
            })
            dialog.setContentView(R.layout.add_player)
            val createVirtualPlayer = dialog.findViewById<Button>(R.id.add_virtual_player)
            val game = chatViewModel.getGameSession()
            val nbPlayers = game.value?.players?.size
            if(game.value?.mode == GameMode.COOP || (nbPlayers != null && nbPlayers >= 8) ) {
                createVirtualPlayer.isEnabled = false
                createVirtualPlayer.alpha = 0.5f
            }
            else {
                createVirtualPlayer.isEnabled = true
                createVirtualPlayer.alpha = 1.0f
            }
            createVirtualPlayer.setOnClickListener {
                chatViewModel.addVirtualPlayer {
                    if (it.isSuccess == true) {
                        Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.success),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (it.isSuccess == false) {
                        Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.error),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }
                dialog.dismiss()
            }
            val listAddPlayer = dialog.findViewById<ListView>(R.id.list_add_player)
            listAddPlayer.adapter = addPlayerAdapter
            dialog.show()
        }
        //Add channel
        addBtn.setOnClickListener {
            val dialog = Dialog(requireContext())
            val joinableChannel = chatViewModel.getJoinableChannels()
            joinChannelAdapter.set(joinableChannel as ArrayList<ChannelData>)
            dialog.setContentView(R.layout.create_join_channel)
            val listJoinChannel = dialog.findViewById<ListView>(R.id.list_join_channel)
            val createChannelButtonElement = dialog.findViewById<Button>(R.id.create_channel)
            createChannelButtonElement.setOnClickListener {
                val channelNameElement = dialog.findViewById<EditText>(R.id.create_channel_name)
                val channelName = channelNameElement.text.toString()

                if (channelName.isNotBlank()) {
                    chatViewModel.createChannel(channelName)
                    dialog.dismiss()
                }
            }
            listJoinChannel.adapter = joinChannelAdapter
            dialog.show()
        }

        channelListElement.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, childView, position, id ->
                val channel: ChannelData =
                    channelListElement.getItemAtPosition(position) as ChannelData
                chatViewModel.setCurrentChannelID(channel.channelID)

                if (channel.gameID != -1.0) {
                    startGameBtn.visibility = View.VISIBLE
                    addPlayerBtn.visibility = View.VISIBLE
                } else {
                    startGameBtn.visibility = View.GONE
                    addPlayerBtn.visibility = View.GONE
                }
            }
        return root
    }

    fun areAtLeast2ParticipantReel(participants: List<ChannelParticipant>):Boolean{
        var nbParticipant =0
        for(participant in participants){
            var isVirtual = participant.isVirtual
            if(isVirtual!=null){
                if(!isVirtual){
                    nbParticipant++
                }
            }
        }
        if(nbParticipant>=2){
            return true
        }

        return false
    }
}