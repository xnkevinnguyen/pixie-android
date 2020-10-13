package com.pixie.android.ui.draw.channelList

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.EnterChannelMutation
import com.pixie.android.R
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.ui.chat.ChatViewModel
import com.pixie.android.ui.chat.UserChannelAdapter
import com.pixie.android.utilities.InjectorUtils
import kotlinx.coroutines.channels.Channel

class ChannelFragment: Fragment() {

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

        val userChannelAdapter = UserChannelAdapter(requireContext())
        channelListElement.adapter = userChannelAdapter

        val factoryChat = InjectorUtils.provideChatViewModelFactory()
        val chatViewModel = ViewModelProvider(this,factoryChat).get(ChatViewModel::class.java)
        val userChannels = chatViewModel.getUserChannels()
        val joinableChannel = chatViewModel.getJoinableChannels()
        val joinChannelAdapter = ChannelJoinAdapter(requireContext())

        if(userChannels.value !=null){
            userChannelAdapter.set(userChannels.value)
        }
        userChannels.observe(viewLifecycleOwner, Observer { userChannelList->
            userChannelAdapter.set(userChannelList)

        })

        if(joinableChannel.value !=null){
            joinChannelAdapter.set(joinableChannel.value)
        }
        joinableChannel.observe(viewLifecycleOwner, Observer { channelList->
            joinChannelAdapter.set(channelList)
        })

        addBtn.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.create_join_channel)
            val listJoinChannel = dialog.findViewById<ListView>(R.id.list_join_channel)
            listJoinChannel.adapter = joinChannelAdapter
//            val list = List<ChannelParticipant>(1){
//                ChannelParticipant(id = 3.0, username = "jo", isOnline = false)
//            }
//            val channel = ChannelData(channelID = 21.0, channelName= "Channel 21", participantList = list)
//            val channel2 = ChannelData(channelID = 19.0, channelName= "Channel 19", participantList = list)
//            joinChannelAdapter.add(channel)
//            joinChannelAdapter.add(channel2)
            dialog.show()
        }

        channelListElement.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, childView, position, id ->
                val channel:ChannelData = channelListElement.getItemAtPosition(position) as ChannelData
                chatViewModel.setCurrentChannelID(channel.channelID)
                userChannelAdapter.setChannelIdSelected(channel.channelID)
            }
        return root
    }


}