package com.pixie.android.ui.draw.availableGames.adapters

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.pixie.android.R
import com.pixie.android.model.chat.ChannelParticipant

class PlayersInGameAdapter(context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var listOfParticipants = ArrayList<ChannelParticipant>()

    fun add(channelParticipant: ChannelParticipant) {
        this.listOfParticipants.add(channelParticipant)
        notifyDataSetChanged()
    }

    fun set(participantList:ArrayList<ChannelParticipant>){
        Log.d("here", "set players adapter")
        listOfParticipants = participantList
        notifyDataSetChanged()
    }


    fun clear(){
        listOfParticipants.clear()
    }

    override fun getCount(): Int {
        return listOfParticipants.size
    }

    override fun getItem(position: Int): Any {
        return listOfParticipants[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val participant: ChannelParticipant = listOfParticipants[position]
        val rowView = inflater.inflate(R.layout.participant_row, parent, false)
        val usernameParticipant = rowView.findViewById<TextView>(R.id.participant_username)
        usernameParticipant.text = participant.username
        return rowView
    }
}


