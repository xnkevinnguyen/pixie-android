package com.pixie.android.ui.chat

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.pixie.android.R
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.ui.draw.channelList.PlayersViewModel
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils
import kotlin.random.Random


class ChannelParticipantAdapter(context: Context) : BaseAdapter(), Filterable {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var listOfParticipants = ArrayList<ChannelParticipant>()
    var filteredListOfParticipants = ArrayList<ChannelParticipant>()

    private val contextCopy = context
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

    private fun reset() {
        clear()
        listOfParticipants.clear()
    }

    fun set(participantList: ArrayList<ChannelParticipant>) {
        reset()
        participantList.sortByDescending { it.isOnline }
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
        val avatarElement = rowView.findViewById<ImageView>(R.id.avatar_participant)



        if(participant.isVirtual ==true){
            removeVirtualElement.visibility = View.VISIBLE
            removeVirtualElement.setOnClickListener {
                playersViewModel.removeVirtualPlayer(participant.id){
                    if(it.isSuccess){
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

            //Change icon
            avatarElement.background = ContextCompat.getDrawable(contextCopy, R.drawable.circle)
            avatarElement.setImageResource(R.drawable.ic_profile_virtual)

        }else{
            removeVirtualElement.visibility = View.GONE
        }
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


        var backgroundColor: Int? = null
        if (!participant.avatarBackground.isNullOrEmpty()) {
            backgroundColor = Color.parseColor(participant.avatarBackground)
        }
        if (backgroundColor == null) {
            backgroundColor = Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
        }
        avatarElement.backgroundTintList = ColorStateList.valueOf(
            foregroundColor
        )
        avatarElement.setColorFilter(
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