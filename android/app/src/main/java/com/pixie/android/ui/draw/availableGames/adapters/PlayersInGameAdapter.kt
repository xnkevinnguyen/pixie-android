package com.pixie.android.ui.draw.availableGames.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.pixie.android.R
import com.pixie.android.model.chat.ChannelParticipant

class PlayersInGameAdapter(context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var listOfPlayers = ArrayList<ChannelParticipant>()

    fun add(channelParticipant: ChannelParticipant) {
        this.listOfPlayers.add(channelParticipant)
        notifyDataSetChanged()
    }

    fun set(participantList:ArrayList<ChannelParticipant>){
        listOfPlayers = participantList
        notifyDataSetChanged()
    }


    fun clear(){
        listOfPlayers.clear()
    }

    override fun getCount(): Int {
        return listOfPlayers.size
    }

    override fun getItem(position: Int): Any {
        return listOfPlayers[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val participant: ChannelParticipant = listOfPlayers[position]
        val rowView = inflater.inflate(R.layout.participant_row, parent, false)
        val usernameParticipant = rowView.findViewById<TextView>(R.id.participant_username)
        usernameParticipant.text = participant.username
        val removeVirtualElement = rowView.findViewById<TextView>(R.id.remove_virtual_player)

        if(participant.isVirtual == false){
            removeVirtualElement.visibility = View.GONE
        }
        return rowView
    }
}


