package com.pixie.android.ui.draw.gameInformation

import android.app.Activity
import android.content.Context
import android.content.res.Resources.Theme
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
import com.pixie.android.R
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.game.GamePlayerData
import com.pixie.android.model.game.GameSessionData
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils
import kotlin.properties.Delegates


class ListPlayerAdapter(context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var playersList = ArrayList<GamePlayerData>()
    private var drawerID: Double = 0.0

    fun setDrawer(drawerPlayer:Double?){
        Log.d("here","drawer $drawerPlayer")
        if(drawerPlayer != null) {
            drawerID = drawerPlayer
            notifyDataSetChanged()
        }
    }

    fun add(player: GamePlayerData) {
        this.playersList.add(player)
        notifyDataSetChanged()

    }
    fun set(newPlayersList:ArrayList<GamePlayerData>){
        playersList= newPlayersList
        notifyDataSetChanged()
    }
    fun clear(){
        playersList.clear()
    }

    override fun getCount(): Int {
        return playersList.size
    }

    override fun getItem(position: Int): Any {
        return playersList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val player: GamePlayerData = playersList[position]
        val rowView = inflater.inflate(R.layout.game_player_row, parent, false)
        val playerUsername = rowView.findViewById<TextView>(R.id.player_username)
        val drawer = rowView.findViewById<ImageView>(R.id.drawer_player)
        val playerScore = rowView.findViewById<TextView>(R.id.player_points)

        playerUsername.text = player.username
        playerScore.text = player.score.toString()

        if(drawerID == player.id){
            drawer.visibility = View.VISIBLE
        }

        return rowView

    }




}