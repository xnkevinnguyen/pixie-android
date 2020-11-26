package com.pixie.android.ui.draw.channelList

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
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
                } else {
                    startGameBtn.visibility = View.GONE
                    leaveGameBtn.visibility = View.GONE
                    addPlayerBtn.visibility = View.GONE
                }
            }
        })

        userChannels.observe(viewLifecycleOwner, Observer { userChannelList ->
            userChannelAdapter.set(userChannelList)
        })

        val gameInfoFactory = InjectorUtils.provideGameInformationViewModelFactory()
        val gameInfoViewModel = ViewModelProvider(this, gameInfoFactory).get(GameInformationViewModel::class.java)
        gameInfoViewModel.getNumberOfParticipantsInGame().observe(viewLifecycleOwner, Observer {
            if(it < 2){
                startGameBtn.isEnabled = false
                startGameBtn.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.dark_dark_grey))
            }else {
                startGameBtn.isEnabled = true
                val typedValue = TypedValue()
                val theme = requireContext().theme
                theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
                @ColorInt val color = typedValue.data
                startGameBtn.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), color)
                )
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
            val channelID = channelViewModel.getCurrentChannelInfo(currentChannelID.value)?.channelID
            if (channelID != null) {
                chatViewModel.exitChannel(channelID)
            }
            if(gameID != null){
                chatViewModel.exitGame(gameID)
            }
        }

        //handles if someone else started the game
        val gameSession = chatViewModel.getGameSession()
        gameSession.observe(viewLifecycleOwner, Observer {
            if (it.status.equals(GameStatus.STARTED)) {

                val navController =
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                navController.navigate(R.id.nav_drawing)


            }
        })
        //add players
        addPlayerBtn.setOnClickListener {
            val dialog = Dialog(requireContext())

            val listUsers = channelViewModel.getAllUsers()
            listUsers.observe(viewLifecycleOwner, Observer { listUserFollow ->
                addPlayerAdapter.set(listUserFollow)
            })
            dialog.setContentView(R.layout.add_player)
            val createVirtualPlayer = dialog.findViewById<Button>(R.id.add_virtual_player)
            val game = chatViewModel.getGameSession()
            if(game.value?.mode == GameMode.COOP){
                createVirtualPlayer.isEnabled = false
                createVirtualPlayer.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.dark_dark_grey))
            }else {
                createVirtualPlayer.isEnabled = true
                val typedValue = TypedValue()
                val theme = requireContext().theme
                theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
                @ColorInt val color = typedValue.data
                createVirtualPlayer.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), color)
                )
            }
            createVirtualPlayer.setOnClickListener {
                chatViewModel.addVirtualPlayer{
                        if(it.isSuccess ==true){
                            Toast.makeText(requireContext(),
                                resources.getString(R.string.success),
                                Toast.LENGTH_LONG).show()
                        }else if( it.isSuccess==false){
                            Toast.makeText(requireContext(),
                                resources.getString(R.string.error),
                                Toast.LENGTH_LONG).show()
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
                chatViewModel.createChannel(channelNameElement.text.toString())
                dialog.dismiss()
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

}