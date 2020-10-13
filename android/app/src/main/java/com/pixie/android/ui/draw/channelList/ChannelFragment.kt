package com.pixie.android.ui.draw.channelList

import android.app.Dialog
import android.os.Bundle
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
import com.pixie.android.R
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.ui.chat.ChatViewModel
import com.pixie.android.ui.chat.UserChannelAdapter
import com.pixie.android.utilities.InjectorUtils

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

        if(userChannels.value !=null){
            userChannelAdapter.set(userChannels.value)
        }
        userChannels.observe(viewLifecycleOwner, Observer { userChannelList->
            userChannelAdapter.set(userChannelList)

        })

        addBtn.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.create_join_channel)
            val listJoinChannel = dialog.findViewById<ListView>(R.id.list_join_channel)
            val joinChannelAdapter = ChannelJoinAdapter(requireContext())
            listJoinChannel.adapter = joinChannelAdapter

            dialog.show()
        }

        channelListElement.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, childView, position, id ->
                val channel:ChannelData = channelListElement.getItemAtPosition(position) as ChannelData
               chatViewModel.setCurrentChannelID(channel.channelID)
                //TODO Make UI changes to show channel is selected

            }
        return root
    }


}