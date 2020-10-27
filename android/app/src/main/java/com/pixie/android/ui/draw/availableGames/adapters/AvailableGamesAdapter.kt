package com.pixie.android.ui.draw.availableGames.adapters

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.pixie.android.R
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.game.AvailableGameData
import com.pixie.android.ui.chat.ChatViewModel
import com.pixie.android.ui.draw.availableGames.AvailableGamesViewModel
import com.pixie.android.utilities.InjectorUtils

class AvailableGamesAdapter(context:Context, activity: Activity): RecyclerView.Adapter<AvailableGamesAdapter.ViewHolder>() {

    private val viewContext = context
    private val viewActivity = activity

    private var listOfGames = ArrayList<AvailableGameData>()

    fun add(game: AvailableGameData) {
        this.listOfGames.add(game)
        notifyDataSetChanged()

    }
    fun set(gamesList:ArrayList<AvailableGameData>?){
        if(gamesList!=null) {
            listOfGames= gamesList
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewContext).inflate(R.layout.available_game_colunm, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val game: AvailableGameData = listOfGames[position]
        val listPlayerAdapter = PlayersInGameAdapter(viewContext)

        val gameNumberString = "Game " + (position+1).toString()
        viewHolder.gameNumber.text = gameNumberString
        viewHolder.mode.text = game.gameData.mode.rawValue
        if(game.gameData.language.rawValue == "ENGLISH") {
            viewHolder.language.setImageDrawable(ContextCompat.getDrawable(viewContext, R.drawable.ic_uk_flag))
        } else{
            viewHolder.language.setImageDrawable(ContextCompat.getDrawable(viewContext, R.drawable.ic_flag_of_france))
        }
        viewHolder.numPlayer.text = game.gameData.listPlayers?.size.toString()

        viewHolder.listPlayer.adapter = listPlayerAdapter
        listPlayerAdapter.set(ArrayList(game.gameData.listPlayers))

        viewHolder.listPlayer.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, pos, _ ->
                val user: ChannelParticipant =
                    viewHolder.listPlayer.getItemAtPosition(pos) as ChannelParticipant
                val dialog = Dialog(viewContext)
                dialog.setContentView(R.layout.other_user_info)
                dialog.findViewById<TextView>(R.id.user_name).text = user.username
                val follow = dialog.findViewById<Button>(R.id.follow)
                follow.setOnClickListener {
                    if (follow.text == "Follow") {
                        follow.text = viewContext.resources.getString(R.string.unfollow)
                    } else follow.text = viewContext.resources.getString(R.string.follow)
                }

                dialog.show()
            }

        val gameFactory = InjectorUtils.provideAvailableGamesViewModelFactory()
        val gameViewModel = ViewModelProvider(ViewModelStore(), gameFactory).get(AvailableGamesViewModel::class.java)

        val chatFactory = InjectorUtils.provideChatViewModelFactory()
        val chatViewModel = ViewModelProvider(ViewModelStore(), chatFactory).get(ChatViewModel::class.java)
        viewHolder.joinBtn.setOnClickListener {
            val gameData = gameViewModel.joinGame(game.gameId)
            if (gameData != null) {
                chatViewModel.setCurrentChannelID(gameData.gameChannelData.channelID)
            }
            val navController = viewActivity.findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_home)
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
        val joinBtn:Button = view.findViewById(R.id.join_game_btn)
    }
}



