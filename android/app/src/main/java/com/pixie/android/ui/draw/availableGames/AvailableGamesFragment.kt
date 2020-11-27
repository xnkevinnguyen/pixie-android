package com.pixie.android.ui.draw.availableGames

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pixie.android.R
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
    private lateinit var availableGamesViewModel: AvailableGamesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.available_game_fragment, container, false)

        preferencesGame = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCES_GAME, Context.MODE_PRIVATE)
        editorGame = preferencesGame.edit()

        val factory = InjectorUtils.provideAvailableGamesViewModelFactory()
        availableGamesViewModel = ViewModelProvider(this, factory).get(AvailableGamesViewModel::class.java)

        val chatFactory = InjectorUtils.provideChatViewModelFactory()
        val chatViewModel = ViewModelProvider(this, chatFactory).get(ChatViewModel::class.java)

        val mode = root.findViewById<TextView>(R.id.mode)
        val difficulty = root.findViewById<TextView>(R.id.level)
        val spinnerLanguage =  root.findViewById<Spinner>(R.id.spinner_language_game)
        val createBtn = root.findViewById<Button>(R.id.create)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val availableGames = root.findViewById<RecyclerView>(R.id.available_games)
        availableGames.layoutManager = layoutManager

        val availableGamesAdapter = AvailableGamesAdapter(requireContext(), requireActivity())
        availableGames.adapter = availableGamesAdapter

        val modeType = preferencesGame.getString(Constants.GAME_MODE, "Free for all")
        val gameMode = "Mode: $modeType"
        mode.text = gameMode

        val difficultyType = preferencesGame.getString(Constants.GAME_DIFF, "Medium")
        val level = "Difficulty: $difficultyType"
        difficulty.text = level

        availableGamesViewModel.fetchAvailableGames(getGameMode(), getGameDifficulty())

        val availableGamesList = availableGamesViewModel.getAvailableGames()
        availableGamesAdapter.set(availableGamesList.value)

        availableGamesList.observe(viewLifecycleOwner, Observer { availableGamesArray->
            availableGamesAdapter.set(availableGamesArray)
        })

        val itemsLang = arrayOf(resources.getString(R.string.eng), resources.getString(R.string.fr))
        val adapterLang: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            R.layout.spinner_layout,
            itemsLang
        )
        spinnerLanguage.adapter = adapterLang

        if(chatViewModel.isUserInAGame()){
            createBtn.isEnabled = false
            createBtn.alpha = 0.5f
        }else {
            createBtn.isEnabled = true
            createBtn.alpha = 1.0f
        }
        createBtn.setOnClickListener {
            // Forcing value in preferences to always be in English and not change because of the language change
            val inputValue = if(spinnerLanguage.selectedItem.toString() == requireContext().resources.getString(R.string.eng)) "English"
            else "French"
            editorGame.putString(Constants.GAME_LANGUAGE, inputValue)
            editorGame.apply()

            val gameData = availableGamesViewModel.createGame(getGameMode(), getGameDifficulty(), getGameLanguage())

            if (gameData != null) {

                gameData?.channelID?.let { id ->
                    chatViewModel.setCurrentChannelID(
                        id
                    )
                }
            }

            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_chat)
        }
        return root
    }

    private fun getGameMode(): GameMode{
        return if (preferencesGame.getString(Constants.GAME_MODE, "Free for all") == "Free for all") GameMode.FREEFORALL
        else if(preferencesGame.getString(Constants.GAME_MODE, "Free for all") == "Solo") GameMode.SOLO
        else GameMode.COOP
    }
    private fun getGameDifficulty(): GameDifficulty{
        return if (preferencesGame.getString(Constants.GAME_DIFF, "Medium") == "Easy") GameDifficulty.EASY
        else if(preferencesGame.getString(Constants.GAME_DIFF, "Medium") == "Medium") GameDifficulty.MEDIUM
        else GameDifficulty.HARD
    }

    private fun getGameLanguage(): Language{
        return if (preferencesGame.getString(Constants.GAME_LANGUAGE, "English")== "English") Language.ENGLISH
        else Language.FRENCH
    }


}