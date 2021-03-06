package com.pixie.android.ui.chat

import android.app.Activity
import android.content.Context
import android.content.res.Resources.Theme
import android.graphics.Color
import android.media.MediaPlayer
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.pixie.android.R
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils


class UserChannelAdapter(context: Context, activity: Activity) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val context = context
    val activity = activity
    private var channelList = ArrayList<ChannelData>()
    private var channelIdSelected:Double = Constants.MAIN_CHANNEL_ID
    private var channelUnread = ArrayList<Double>()

    fun setChannelIdSelected(id:Double){
        channelIdSelected = id
        notifyDataSetChanged()
    }


    fun add(channel: ChannelData) {
        this.channelList.add(channel)
        notifyDataSetChanged()

    }
    fun set(newChannelList:LinkedHashMap<Double,ChannelData>?){
        if(newChannelList!=null) {
            channelList= ArrayList(newChannelList.map{
                it.value
            })
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

        val channelName = rowView.findViewById<TextView>(R.id.channel_name)
        channelName.text = channel.channelName

        val exit = rowView.findViewById<TextView>(R.id.exit_channel)

        if(channel.channelID == Constants.MAIN_CHANNEL_ID){
            exit.visibility = View.GONE
        }

        val badge = rowView.findViewById<ImageView>(R.id.chat_notification_badge)
        if(channelIdSelected == channel.channelID){
            badge.visibility = View.GONE
            val typedValue = TypedValue()
            val theme: Theme = context.getTheme()
            theme.resolveAttribute(R.attr.color, typedValue, true)
            @ColorInt val color = typedValue.data
            rowView.setBackgroundColor(color)
            //channelName.setTextColor(Color.WHITE)
        }
        val factoryChat = InjectorUtils.provideChatViewModelFactory()
        val chatViewModel = ViewModelProvider(ViewModelStore(),factoryChat).get(ChatViewModel::class.java)

        exit.setOnClickListener {
            chatViewModel.exitChannel(channel.channelID)
            val gameID = channel.gameID
            if(gameID != null){
                chatViewModel.exitGame(gameID)
            }
        }

        //set nb of unread messages
        val preferencesSettings = context.getSharedPreferences(Constants.SHARED_PREFERENCES_SETTING, Context.MODE_PRIVATE)
        val soundOn:Boolean = preferencesSettings.getBoolean(Constants.NOTIFICATION, true)
        val mediaPlayer = chatViewModel.createMediaPlayer(R.raw.notification, context)
        val unreadMessages = rowView.findViewById<TextView>(R.id.unread_messages)
        unreadMessages.text = channel.unreadMessages.toString()
        if(channel.unreadMessages>0){
            if(soundOn)chatViewModel.startMediaPlayer(mediaPlayer)
            else chatViewModel.releaseMediaPlayer(mediaPlayer)
            badge.visibility = View.VISIBLE
        }
        return rowView

    }




}