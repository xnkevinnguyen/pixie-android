package com.pixie.android.ui.chat

import android.content.Context
import android.content.res.Resources.Theme
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.pixie.android.R
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.utilities.InjectorUtils


class UserChannelAdapter(context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val context = context
    private var channelList = ArrayList<ChannelData>()
    private var channelIdSelected:Double = 1.0

    fun setChannelIdSelected(id:Double){
        channelIdSelected = id
        notifyDataSetChanged()
    }

    fun add(channel: ChannelData) {
        this.channelList.add(channel)
        notifyDataSetChanged()

    }
    fun set(newChannelList:ArrayList<ChannelData>?){
        if(newChannelList!=null) {
            channelList= ArrayList(newChannelList)
        }
        notifyDataSetChanged()
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
        val rowView = inflater.inflate(R.layout.user_channel_row_, parent, false)
        val exit = rowView.findViewById<TextView>(R.id.exit_channel)
        if(channel.channelID == 1.0){
            exit.visibility = View.GONE
        }

        if(channelIdSelected == channel.channelID){
            val typedValue = TypedValue()
            val theme: Theme = context.getTheme()
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
            @ColorInt val color = typedValue.data
            rowView.setBackgroundColor(color)
        }

        exit.setOnClickListener {
            val factoryChat = InjectorUtils.provideChatViewModelFactory()
            val chatViewModel = ViewModelProvider(ViewModelStore(),factoryChat).get(ChatViewModel::class.java)
            chatViewModel.exitChannel(channel.channelID)
        }
        val channelName = rowView.findViewById<TextView>(R.id.participant_username)
        channelName.text = channel.channelName
        return rowView

    }




}