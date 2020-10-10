package com.pixie.android.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.pixie.android.R
import com.pixie.android.model.chat.ChannelData


class UserChannelAdapter(context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var channelList = ArrayList<ChannelData>()

    fun add(channel: ChannelData) {
        this.channelList.add(channel)

    }
    fun set(newChannelList:ArrayList<ChannelData>){
        channelList = newChannelList
    }
    fun clear(){
        channelList.clear()
    }

    override fun getCount(): Int {
        return channelList.size
    }

    override fun getItem(position: Int): Any {
        return channelList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val channel: ChannelData = channelList[position]
        val rowView = inflater.inflate(R.layout.participant_row, parent, false)
        val channelName = rowView.findViewById<TextView>(R.id.participant_username)
        channelName.text = channel.channelName
        return rowView

    }




}