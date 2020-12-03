package com.pixie.android.ui.draw.availableGames.adapters

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.Log
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
import com.pixie.android.model.game.CreatedGameData
import com.pixie.android.ui.chat.ChatViewModel
import com.pixie.android.ui.draw.availableGames.AvailableGamesViewModel
import com.pixie.android.ui.draw.channelList.PlayersViewModel
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils

class AvailableGamesAdapter(context:Context, activity: Activity): RecyclerView.Adapter<AvailableGamesAdapter.ViewHolder>() {

    private val viewContext = context
    private val viewActivity = activity

    private var listOfGames = ArrayList<CreatedGameData>()

    fun add(game: CreatedGameData) {
        this.listOfGames.add(game)
        notifyDataSetChanged()

    }
    fun set(gamesList:ArrayList<CreatedGameData>?){
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
        val game: CreatedGameData = listOfGames[position]
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
        val players = game.gameData.listPlayers
        if(!players.isNullOrEmpty()) {
            listPlayerAdapter.set(ArrayList(players))
        }

        val gameFactory = InjectorUtils.provideAvailableGamesViewModelFactory()
        val gameViewModel = ViewModelProvider(ViewModelStore(), gameFactory).get(AvailableGamesViewModel::class.java)

        val chatFactory = InjectorUtils.provideChatViewModelFactory()
        val chatViewModel = ViewModelProvider(ViewModelStore(), chatFactory).get(ChatViewModel::class.java)
//        if(chatViewModel.isUserInAGame()){
//            viewHolder.joinBtn.isEnabled = false
//            viewHolder.joinBtn.alpha = 0.5f
//        }else{
//            viewHolder.joinBtn.isEnabled = true
//            viewHolder.joinBtn.alpha = 1.0f
//        }
        viewHolder.joinBtn.setOnClickListener {
            if(chatViewModel.isUserInAGame()) {
                val dialog = Dialog(viewContext)
                dialog.setContentView(R.layout.leave_game_dialog)
                val dismissButton = dialog.findViewById<Button>(R.id.dismiss)
                val leaveGameButton = dialog.findViewById<Button>(R.id.leave)

                dismissButton.setOnClickListener {
                    dialog.dismiss()
                }

                leaveGameButton.setOnClickListener {
                    val gameData = chatViewModel.getUserGameData()
                    if (gameData != null) {
                        val gameId = gameData.gameID
                        if (gameId != null) {
                            chatViewModel.exitGame(gameId)
                            chatViewModel.exitChannel(gameData.channelID)
                        }
                    }

                    val game = gameViewModel.joinGame(game.gameId)
                    game?.channelID?.let { id ->
                        chatViewModel.setCurrentChannelID(
                            id
                        )
                    }
                    val navController = viewActivity.findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.nav_chat)

                    dialog.dismiss()
                }
                dialog.show()
            } else {
                val gameData = gameViewModel.joinGame(game.gameId)
                gameData?.channelID?.let { id ->
                    chatViewModel.setCurrentChannelID(
                        id
                    )
                }
                val navController = viewActivity.findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.nav_chat)
            }
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

    fun isUser(user: ChannelParticipant): Boolean{
        val preferences = viewContext.getSharedPreferences(Constants.SHARED_PREFERENCES_LOGIN, Context.MODE_PRIVATE)
        val userIDPreference = preferences.getString(Constants.USER_ID, null)
        if(userIDPreference.toDouble() == user.id){
            return true
        }
        return false
    }
}



