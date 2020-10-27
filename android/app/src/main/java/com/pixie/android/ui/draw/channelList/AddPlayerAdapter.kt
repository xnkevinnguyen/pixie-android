package com.pixie.android.ui.draw.channelList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.pixie.android.R
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.ui.chat.ChatViewModel
import com.pixie.android.utilities.InjectorUtils

class AddPlayerAdapter(context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val context = context

    private var listPlayersToAdd = ArrayList<ChannelParticipant>()

    fun add(user: ChannelParticipant) {
        this.listPlayersToAdd.add(user)
        notifyDataSetChanged()
    }

    fun set(newParticipantList:ArrayList<ChannelParticipant>?){
        if(newParticipantList!=null) {
            listPlayersToAdd= ArrayList(newParticipantList)
        }
        notifyDataSetChanged()
    }

    fun clear(){
        listPlayersToAdd.clear()
    }

    override fun getCount(): Int {
        return listPlayersToAdd.size
    }

    override fun getItem(position: Int): Any {
        return listPlayersToAdd[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val channel: ChannelParticipant = listPlayersToAdd[position]
        val rowView = inflater.inflate(R.layout.add_player_row, parent, false)
        val userName = rowView.findViewById<TextView>(R.id.username_to_add)
        userName.text = channel.username

        val inviteBtn = rowView.findViewById<Button>(R.id.invite)
        inviteBtn.setOnClickListener {
        }
        return rowView

    }

}