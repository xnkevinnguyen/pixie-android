package com.pixie.android.ui.chat

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.pixie.android.R
import com.pixie.android.model.chat.ChannelParticipant


class ChannelParticipantAdapter(context: Context) : BaseAdapter(), Filterable {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var listOfParticipants = ArrayList<ChannelParticipant>()
    var filteredListOfParticipants = ArrayList<ChannelParticipant>()

    fun add(channelParticipant: ChannelParticipant) {
        this.listOfParticipants.add(channelParticipant)
        this.filteredListOfParticipants.add(channelParticipant)
        notifyDataSetChanged()
    }
    fun clear(){
        filteredListOfParticipants.clear()
    }
    fun reset(){
        clear()
        listOfParticipants.clear()
    }

    fun set(participantList:ArrayList<ChannelParticipant>){
        reset()
        listOfParticipants = ArrayList(participantList)
        filteredListOfParticipants=ArrayList(participantList)
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
        participantUserName.text = participant.username
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