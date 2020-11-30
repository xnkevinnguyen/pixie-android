package com.pixie.android.ui.draw.gameInformation

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.pixie.android.R
import com.pixie.android.model.game.GameParticipant


class ListPlayerAdapter(context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var playersList = ArrayList<GameParticipant>()
    private var drawerID: Double = 0.0

    fun setDrawer(drawerPlayer:Double?){
        Log.d("here","drawer $drawerPlayer")
        if(drawerPlayer != null) {
            drawerID = drawerPlayer
            notifyDataSetChanged()
        }
    }

    fun add(player: GameParticipant) {
        this.playersList.add(player)
        notifyDataSetChanged()

    }
    fun set(newPlayersList:ArrayList<GameParticipant>){
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
        val player: GameParticipant = playersList[position]
        val rowView = inflater.inflate(R.layout.game_player_row, parent, false)
        val playerUsername = rowView.findViewById<TextView>(R.id.player_username)
        val drawer = rowView.findViewById<ImageView>(R.id.drawer_player)
        val playerScore = rowView.findViewById<TextView>(R.id.player_points)

        playerUsername.text = player.username
        playerScore.text = player.score.toInt().toString()

        if(player.isVirtual){
            playerScore.text ="-"
        }

        if(drawerID == player.id){
            drawer.visibility = View.VISIBLE
        }

        return rowView

    }




}