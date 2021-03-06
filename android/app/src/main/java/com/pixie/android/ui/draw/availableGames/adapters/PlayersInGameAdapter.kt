package com.pixie.android.ui.draw.availableGames.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.pixie.android.R
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.ui.draw.channelList.PlayersViewModel
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils
import kotlin.random.Random

class PlayersInGameAdapter(context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var listOfPlayers = ArrayList<ChannelParticipant>()
    val factory = InjectorUtils.providePlayersViewModelFactory()
    private val playersViewModel = ViewModelProvider(ViewModelStore(), factory).get(PlayersViewModel::class.java)

    private val contextCopy = context

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
        removeVirtualElement.visibility = View.GONE

        val avatarElement = rowView.findViewById<ImageView>(R.id.avatar_participant)

        if(participant.isVirtual ==true){
            //Change icon
            avatarElement.setImageDrawable(ContextCompat.getDrawable(contextCopy, R.drawable.ic_profile_virtual))
        }

        var foregroundColor: Int? = null
        if (!participant.avatarForeground.isNullOrEmpty()) {
            foregroundColor = Color.parseColor(participant.avatarForeground)
        }
        if (foregroundColor == null) {
            foregroundColor = Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
        }


        var backgroundColor: Int? = null
        if (!participant.avatarBackground.isNullOrEmpty()) {
            backgroundColor = Color.parseColor(participant.avatarBackground)
        }
        if (backgroundColor == null) {
            backgroundColor = Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
        }

        avatarElement.setColorFilter(
            backgroundColor
        )
        avatarElement.backgroundTintList = ColorStateList.valueOf(
            foregroundColor
        )

        return rowView
    }
}


