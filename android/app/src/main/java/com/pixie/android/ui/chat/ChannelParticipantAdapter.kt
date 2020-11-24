package com.pixie.android.ui.chat

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.pixie.android.R
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.ui.draw.channelList.PlayersViewModel
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils
import kotlin.coroutines.coroutineContext
import kotlin.random.Random


class ChannelParticipantAdapter(context: Context) : BaseAdapter(), Filterable {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var listOfParticipants = ArrayList<ChannelParticipant>()
    var filteredListOfParticipants = ArrayList<ChannelParticipant>()

    val factory = InjectorUtils.providePlayersViewModelFactory()
    private val playersViewModel = ViewModelProvider(ViewModelStore(), factory).get(PlayersViewModel::class.java)

    fun add(channelParticipant: ChannelParticipant) {
        if (channelParticipant.isOnline == true) {
            this.listOfParticipants.add(channelParticipant)
            this.filteredListOfParticipants.add(channelParticipant)
            notifyDataSetChanged()
        }
    }

    fun clear() {
        filteredListOfParticipants.clear()
    }

    fun reset() {
        clear()
        listOfParticipants.clear()
    }

    fun set(participantList: ArrayList<ChannelParticipant>) {
        reset()
        val listOfFriends = playersViewModel.getFriendList().value
        participantList.sortByDescending { it.isOnline }
        participantList.sortByDescending { listOfFriends?.contains(it) }
        participantList.sortByDescending { playersViewModel.getUser().userId==it.id }
        listOfParticipants = participantList
        filteredListOfParticipants = participantList
        notifyDataSetChanged()

    }

    override fun getCount(): Int {
        return filteredListOfParticipants.size
    }

    override fun getItem(position: Int): Any {
        return filteredListOfParticipants[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val participant: ChannelParticipant = filteredListOfParticipants[position]
        val rowView = inflater.inflate(R.layout.participant_row, parent, false)
        val participantUserName = rowView.findViewById<TextView>(R.id.participant_username)
        val removeVirtualElement = rowView.findViewById<TextView>(R.id.remove_virtual_player)

        if(participant.isVirtual ==true){
            removeVirtualElement.visibility = View.VISIBLE
            removeVirtualElement.setOnClickListener {
                playersViewModel.removeVirtualPlayer(participant.id){
                    if(it.isSuccess ==true){
                        Toast.makeText(rowView.context,
                            rowView.context.resources.getString(R.string.success),
                            Toast.LENGTH_LONG).show()
                    }else if(!it.isSuccess){
                        Toast.makeText(rowView.context,
                            rowView.context.resources.getString(R.string.error),
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
        }else{
            removeVirtualElement.visibility = View.GONE
        }
        participantUserName.text = participant.username

        val onlineIconElement = rowView.findViewById<ImageView>(R.id.online_badge)
        if (participant.isOnline == false) {
            onlineIconElement.setColorFilter(Color.GRAY)
        }
        val avatarElement = rowView.findViewById<ImageView>(R.id.avatar_participant)


        avatarElement.setColorFilter(
            Color.argb(
                255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(
                    256
                )
            )
        )

        avatarElement.backgroundTintList = ColorStateList.valueOf(
            Color.argb(
                255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(
                    256
                )
            )
        )

        val ringElement = rowView.findViewById<ImageView>(R.id.avatar_ring)
        if(playersViewModel.getFriendList().value?.contains(participant)==true){
            ringElement.backgroundTintList = ColorStateList.valueOf(Color.parseColor(Constants.AVATAR_RING_COLOR_YELLOW))
        }else if(playersViewModel.getUser().userId == participant.id){
            ringElement.backgroundTintList = ColorStateList.valueOf(Color.parseColor(Constants.AVATAR_RING_COLOR_BLUE))

        }
        else{
            ringElement.backgroundTintList = ColorStateList.valueOf(Color.parseColor(Constants.AVATAR_RING_COLOR_SILVER))

        }
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
                    filteredListOfParticipants = results.values as ArrayList<ChannelParticipant>
                    notifyDataSetChanged()
                }
            }

            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filterString = constraint.toString().toLowerCase()
                val results = FilterResults()
                val count = listOfParticipants.size
                var filterableString: ChannelParticipant
                if (TextUtils.isEmpty(constraint)) {
                    results.count = listOfParticipants.size
                    results.values = listOfParticipants
                } else {
                    val filteredResults = ArrayList<ChannelParticipant>()
                    for (i in 0 until count) {
                        filterableString = listOfParticipants[i]
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