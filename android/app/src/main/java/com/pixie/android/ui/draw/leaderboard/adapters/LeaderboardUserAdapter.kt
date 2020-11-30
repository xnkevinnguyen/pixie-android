package com.pixie.android.ui.draw.leaderboard.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.pixie.android.R
import com.pixie.android.model.leaderboard.LeaderboardData
import com.pixie.android.ui.draw.channelList.PlayersViewModel
import com.pixie.android.utilities.InjectorUtils
import kotlin.random.Random

class LeaderboardUserAdapter(context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var listOfParticipants = ArrayList<LeaderboardData>()

    fun add(channelParticipant: LeaderboardData) {
        this.listOfParticipants.add(channelParticipant)
        notifyDataSetChanged()
    }

    fun clear() {
        listOfParticipants.clear()
    }

    fun reset() {
        clear()
        listOfParticipants.clear()
    }

    fun set(participantList: ArrayList<LeaderboardData>) {
        reset()
        listOfParticipants = participantList
        notifyDataSetChanged()

    }

    override fun getCount(): Int {
        return listOfParticipants.size
    }

    override fun getItem(position: Int): Any {
        return listOfParticipants[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val participant: LeaderboardData = listOfParticipants[position]
        val rowView = inflater.inflate(R.layout.leaderboard_user_row, parent, false)

        val placeNumber = rowView.findViewById<TextView>(R.id.place_number)
        val placeString = "#" + (position+1)
        placeNumber.text = placeString

        val participantUserName = rowView.findViewById<TextView>(R.id.participant_username)
        participantUserName.text = participant.username

        val participantValue = rowView.findViewById<TextView>(R.id.participant_value)
        participantValue.text = participant.value?.toInt().toString()

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

        return rowView
    }
}