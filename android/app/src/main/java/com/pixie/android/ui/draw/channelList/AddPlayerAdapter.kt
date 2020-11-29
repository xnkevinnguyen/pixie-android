package com.pixie.android.ui.draw.channelList

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.pixie.android.R
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.ui.chat.ChatViewModel
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils
import kotlin.random.Random

class AddPlayerAdapter(context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val context = context

    private var listPlayersToAdd = ArrayList<ChannelParticipant>()

    val factory = InjectorUtils.providePlayersViewModelFactory()
    val playersViewModel = ViewModelProvider(ViewModelStore(), factory).get(PlayersViewModel::class.java)

    fun add(user: ChannelParticipant) {
        this.listPlayersToAdd.add(user)
        notifyDataSetChanged()
    }

    fun set(newParticipantList:ArrayList<ChannelParticipant>){
        val listOfFriends = playersViewModel.getFriendList()
        //newParticipantList.sortByDescending { it.isOnline }
        newParticipantList.sortByDescending { listOfFriends.value?.contains(it) }
        listPlayersToAdd = ArrayList(newParticipantList)
        //listPlayersToAdd= ArrayList(newParticipantList)
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
        val participant: ChannelParticipant = listPlayersToAdd[position]
        val rowView = inflater.inflate(R.layout.add_player_row, parent, false)
        
        val userName = rowView.findViewById<TextView>(R.id.username_to_add)
        userName.text = participant.username
        val factory = InjectorUtils.provideChatViewModelFactory()
        val chatViewModel = ViewModelProvider(ViewModelStore(),factory).get(ChatViewModel::class.java)

        val inviteBtn = rowView.findViewById<Button>(R.id.invite)
        inviteBtn.setOnClickListener {
            chatViewModel.sendGameInvitation(participant.id){
                if(it.isSuccess ==true){
                    Toast.makeText(context,
                        "Success",
                        Toast.LENGTH_LONG).show()
                }else if(!it.isSuccess){
                    Toast.makeText(context,
                        it.error,
                        Toast.LENGTH_LONG).show()
                }
            }

        }

        val onlineIconElement = rowView.findViewById<ImageView>(R.id.online_badge)
        if (participant.isOnline == false) {
            onlineIconElement.setColorFilter(Color.GRAY)
        }
        val avatarElement = rowView.findViewById<ImageView>(R.id.avatar_participant)

        var foregroundColor: Int? = null
        if (!participant.avatarForeground.isNullOrEmpty()) {
            foregroundColor = Color.parseColor(participant.avatarForeground)
        }
        if (foregroundColor == null) {
            foregroundColor = Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
        }
        avatarElement.setColorFilter(
            foregroundColor
        )

        var backgroundColor: Int? = null
        if (!participant.avatarBackground.isNullOrEmpty()) {
            backgroundColor = Color.parseColor(participant.avatarBackground)
        }
        if (backgroundColor == null) {
            backgroundColor = Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
        }
        avatarElement.backgroundTintList = ColorStateList.valueOf(
            backgroundColor
        )

        val ringElement = rowView.findViewById<ImageView>(R.id.avatar_ring)
        if (playersViewModel.getFriendList().value?.contains(participant) == true) {
            ringElement.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(Constants.AVATAR_RING_COLOR_YELLOW))
        } else {
            ringElement.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(Constants.AVATAR_RING_COLOR_SILVER))

        }
        return rowView

    }

}