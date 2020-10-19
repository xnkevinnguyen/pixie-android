package com.pixie.android.ui.draw.availableGames

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pixie.android.R
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.game.AvailableGame
import com.pixie.android.type.GameDifficulty
import com.pixie.android.type.GameMode
import com.pixie.android.type.Language
import com.pixie.android.ui.chat.ChatViewModel
import com.pixie.android.ui.draw.availableGames.adapters.AvailableGamesAdapter
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils


class AvailableGamesFragment : Fragment() {

    private lateinit var preferencesGame: SharedPreferences
    private lateinit var editorGame: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.available_game_fragment, container, false)

        preferencesGame = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCES_GAME, Context.MODE_PRIVATE)
        editorGame = preferencesGame.edit()

        val mode = root.findViewById<TextView>(R.id.mode)
        val difficulty = root.findViewById<TextView>(R.id.level)
        val spinnerLanguage =  root.findViewById<Spinner>(R.id.spinner_language_game)
        val createBtn = root.findViewById<Button>(R.id.create)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val availableGames = root.findViewById<RecyclerView>(R.id.available_games)
        availableGames.layoutManager = layoutManager

        val availableGamesAdapter = AvailableGamesAdapter(requireContext())
        availableGames.adapter = availableGamesAdapter

//        val list = ArrayList<ChannelParticipant>()
//        val parti = ChannelParticipant(id= 35.0, username = "joe", isOnline = false)
//        val parti2 = ChannelParticipant(id= 30.0, username = "Kevin", isOnline = true)
//        list.add(parti)
//        list.add(parti2)
//
//        val game = AvailableGame(mode = "Free for all", language = "ENGLISH", listPlayers = list)
//        val game2 = AvailableGame(mode = "Free for all", language = "FRENCH", listPlayers = list)
//        val game3 = AvailableGame(mode = "Free for all", language = "ENGLISH", listPlayers = list)
//        val game4 = AvailableGame(mode = "Free for all", language = "FRENCH", listPlayers = list)
//        availableGamesAdapter.add(game)
//        availableGamesAdapter.add(game2)
//        availableGamesAdapter.add(game3)
//        availableGamesAdapter.add(game4)

        val modeType = preferencesGame.getString(Constants.GAME_MODE, "Free for all")

        val gameMode = "Mode: $modeType"
        mode.text = gameMode

        val level = "Difficulty: " + preferencesGame.getString(Constants.GAME_DIFF, "Medium")
        difficulty.text = level

        val itemsLang = arrayOf(resources.getString(R.string.eng), resources.getString(R.string.fr))
        val adapterLang: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            itemsLang
        )
        spinnerLanguage.adapter = adapterLang

        createBtn.setOnClickListener {
            editorGame.putString(Constants.GAME_LANGUAGE, spinnerLanguage.selectedItem.toString())
            editorGame.apply()

            val factory = InjectorUtils.provideChatViewModelFactory()
            val chatViewModel = ViewModelProvider(this, factory).get(ChatViewModel::class.java)
            val gameData = chatViewModel.createGame(getGameMode(), getGameDifficulty(), getGameLanguage())

            if (gameData != null) {
                chatViewModel.setCurrentChannelID(gameData.channelData.channelID)
            }

            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_home)

        }
        return root
    }

    private fun getGameMode(): GameMode{
        return if (preferencesGame.getString(Constants.GAME_MODE, "Free for all")== "Free for all") GameMode.FREEFORALL
        else if(preferencesGame.getString(Constants.GAME_MODE, "Free for all")== "Solo") GameMode.SOLO
        else GameMode.COOP
    }
    private fun getGameDifficulty(): GameDifficulty{
        return if (preferencesGame.getString(Constants.GAME_DIFF, "Medium")== "Easy") GameDifficulty.EASY
        else if(preferencesGame.getString(Constants.GAME_DIFF, "Medium")== "Medium") GameDifficulty.MEDIUM
        else GameDifficulty.HARD
    }

    private fun getGameLanguage(): Language{
        return if (preferencesGame.getString(Constants.GAME_LANGUAGE, "English")== "English") Language.ENGLISH
        else Language.FRENCH
    }


}