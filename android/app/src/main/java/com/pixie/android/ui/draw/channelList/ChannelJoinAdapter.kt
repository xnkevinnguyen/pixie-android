package com.pixie.android.ui.draw.channelList

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.pixie.android.R
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant

class ChannelJoinAdapter(context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var listChannelJoin = ArrayList<ChannelData>()

    fun add(channel: ChannelData) {
        this.listChannelJoin.add(channel)
        notifyDataSetChanged()
    }
    fun clear(){
        listChannelJoin.clear()
    }

    override fun getCount(): Int {
        return listChannelJoin.size
    }

    override fun getItem(position: Int): Any {
        return listChannelJoin[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val channel: ChannelData = listChannelJoin[position]
        val rowView = inflater.inflate(R.layout.join_channel_row, parent, false)
        val channelId = rowView.findViewById<TextView>(R.id.channel_id)
        val channelName = rowView.findViewById<TextView>(R.id.channel_name)
        channelId.text = channel.channelID.toString()
        channelName.text = channel.channelName
        return rowView

    }

}