package com.pixie.android.ui.draw.availableGames.adapters

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pixie.android.R
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.game.AvailableGameData
import com.pixie.android.model.game.GameData

class AvailableGamesAdapter(context:Context): RecyclerView.Adapter<AvailableGamesAdapter.ViewHolder>() {

    val context = context

    private var listOfGames = ArrayList<AvailableGameData>()

    fun add(game: AvailableGameData) {
        this.listOfGames.add(game)
        notifyDataSetChanged()

    }
    fun set(gamesList:LinkedHashMap<Double, AvailableGameData>?){
        if(gamesList!=null) {
            listOfGames= ArrayList(gamesList.map{
                it.value
            })
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.available_game_colunm, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val game: AvailableGameData = listOfGames[position]
        val listPlayerAdapter = PlayersInGameAdapter(context)

        viewHolder.gameNumber.text = "Game " + (position+1).toString()
        viewHolder.mode.text = game.gameData.mode.rawValue
        if(game.gameData.language.rawValue == "ENGLISH") {
            viewHolder.language.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_uk_flag))
        } else{
            viewHolder.language.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_flag_of_france))
        }
        viewHolder.numPlayer.text = game.gameData.listPlayers?.size.toString()

        viewHolder.listPlayer.adapter = listPlayerAdapter
        listPlayerAdapter.set(ArrayList(game.gameData.listPlayers))

        viewHolder.listPlayer.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val user: ChannelParticipant =
                    viewHolder.listPlayer.getItemAtPosition(position) as ChannelParticipant
                val dialog = Dialog(context)
                dialog.setContentView(R.layout.other_user_info)
                dialog.findViewById<TextView>(R.id.user_name).text = user.username
                val follow = dialog.findViewById<Button>(R.id.follow)
                follow.setOnClickListener {
                    if (follow.text == "Follow") {
                        follow.text = context.resources.getString(R.string.unfollow)
                    } else follow.text = context.resources.getString(R.string.follow)
                }

                dialog.show()
            }
    }

    override fun getItemCount(): Int {
        return listOfGames.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mode: TextView = view.findViewById(R.id.mode_type)
        val language: ImageView = view.findViewById(R.id.language_icon)
        val numPlayer: TextView = view.findViewById(R.id.number_player)
        val listPlayer: ListView = view.findViewById(R.id.game_player)
        val gameNumber:TextView = view.findViewById(R.id.game_number)
    }
}



