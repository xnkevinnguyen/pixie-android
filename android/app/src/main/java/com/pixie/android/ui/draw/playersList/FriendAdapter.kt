package com.pixie.android.ui.draw.playersList

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.pixie.android.R
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.ui.draw.channelList.PlayersViewModel
import com.pixie.android.utilities.InjectorUtils
import kotlin.random.Random

class FriendAdapter(context: Context) : BaseAdapter(), Filterable {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var listOfFriends = ArrayList<ChannelParticipant>()
    var filteredListOfFriend = ArrayList<ChannelParticipant>()

    private val contextCopy = context
    val factory = InjectorUtils.providePlayersViewModelFactory()
    private val playersViewModel = ViewModelProvider(ViewModelStore(), factory).get(PlayersViewModel::class.java)

    fun add(channelParticipant: ChannelParticipant) {
        if (channelParticipant.isOnline == true) {
            this.listOfFriends.add(channelParticipant)
            this.filteredListOfFriend.add(channelParticipant)
            notifyDataSetChanged()
        }
    }

    fun clear() {
        filteredListOfFriend.clear()
    }

    private fun reset() {
        clear()
        listOfFriends.clear()
    }

    fun set(participantList: ArrayList<ChannelParticipant>) {
        reset()
        Log.d("friend", "friend adapter $participantList")
        participantList.sortByDescending { it.isOnline }
        listOfFriends = participantList
        filteredListOfFriend = participantList
        notifyDataSetChanged()

    }

    override fun getCount(): Int {
        return filteredListOfFriend.size
    }

    override fun getItem(position: Int): Any {
        return filteredListOfFriend[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val participant: ChannelParticipant = filteredListOfFriend[position]
        val rowView = inflater.inflate(R.layout.participant_row, parent, false)
        val participantUserName = rowView.findViewById<TextView>(R.id.participant_username)
        val removeVirtualElement = rowView.findViewById<TextView>(R.id.remove_virtual_player)
        val avatarElement = rowView.findViewById<ImageView>(R.id.avatar_participant)

        removeVirtualElement.visibility = View.GONE
        participantUserName.text = participant.username

        val onlineIconElement = rowView.findViewById<ImageView>(R.id.online_badge)
        if (participant.isOnline == false) {
            onlineIconElement.setColorFilter(Color.GRAY)
        }

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

        return rowView

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(
                constraint: CharSequence,
                results: FilterResults
            ) {
                if (results.count == 0) {
                    notifyDataSetInvalidated();
                } else {
                    filteredListOfFriend = results.values as ArrayList<ChannelParticipant>
                    notifyDataSetChanged()
                }
            }

            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filterString = constraint.toString().toLowerCase()
                val results = FilterResults()
                val count = listOfFriends.size
                var filterableString: ChannelParticipant
                if (TextUtils.isEmpty(constraint)) {
                    results.count = listOfFriends.size
                    results.values = listOfFriends
                } else {
                    val filteredResults = ArrayList<ChannelParticipant>()
                    for (i in 0 until count) {
                        filterableString = listOfFriends[i]
                        if (filterableString.username.toLowerCase().contains(filterString)) {
                            filteredResults.add(filterableString)
                        }
                    }
                    results.values = filteredResults
                    results.count = filteredResults.size
                }
                return results
            }
        }
    }
}