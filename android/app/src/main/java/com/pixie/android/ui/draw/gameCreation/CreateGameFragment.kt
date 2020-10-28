package com.pixie.android.ui.draw.gameCreation

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
import androidx.navigation.Navigation
import com.pixie.android.R
import com.pixie.android.type.GameDifficulty
import com.pixie.android.type.GameMode
import com.pixie.android.type.Language
import com.pixie.android.ui.chat.ChatViewModel
import com.pixie.android.ui.draw.availableGames.AvailableGamesViewModel
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils

class CreateGameFragment: Fragment() {
    private lateinit var preferencesGame: SharedPreferences
    private lateinit var editorGame: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.create_game_fragment, container, false)
        preferencesGame = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCES_GAME, Context.MODE_PRIVATE)
        editorGame = preferencesGame.edit()

        val factoryChat = InjectorUtils.provideChatViewModelFactory()
        val chatViewModel = ViewModelProvider(this,factoryChat).get(ChatViewModel::class.java)

        val availableGamesFactory = InjectorUtils.provideAvailableGamesViewModelFactory()
        val availableGamesViewModel = ViewModelProvider(this, availableGamesFactory).get(AvailableGamesViewModel::class.java)

        val spinnerLanguage =  root.findViewById<Spinner>(R.id.spinner_language_game)
        val difficulty = root.findViewById<TextView>(R.id.difficulty)
        val difString = "Difficulty: " + preferencesGame.getString(Constants.GAME_DIFF, "Medium")
        difficulty.text = difString

        val itemsLang = arrayOf(resources.getString(R.string.eng), resources.getString(R.string.fr))
        val adapterLang: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            itemsLang
        )
        spinnerLanguage.adapter = adapterLang

        val createBtn = root.findViewById<TextView>(R.id.create)
        createBtn.setOnClickListener {
            editorGame.putString(Constants.GAME_LANGUAGE, spinnerLanguage.selectedItem.toString())
            editorGame.apply()

            val gameData = availableGamesViewModel.createGame(getGameMode(), getGameDifficulty(), getGameLanguage())
            if (gameData != null) {
                //succesfully created game
                chatViewModel.startGameSession(gameData.id) {
                    if (it.isSuccess) {
                        val navController =
                            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                        navController.navigate(R.id.nav_drawing)
                    }
                }
            }
        }
        return root
    }

    private fun getGameMode(): GameMode {
        return if (preferencesGame.getString(Constants.GAME_MODE, "Free for all")== "Free for all") GameMode.FREEFORALL
        else if(preferencesGame.getString(Constants.GAME_MODE, "Free for all")== "Solo") GameMode.SOLO
        else GameMode.COOP
    }
    private fun getGameDifficulty(): GameDifficulty {
        return if (preferencesGame.getString(Constants.GAME_DIFF, "Medium")== "Easy") GameDifficulty.EASY
        else if(preferencesGame.getString(Constants.GAME_DIFF, "Medium")== "Medium") GameDifficulty.MEDIUM
        else GameDifficulty.HARD
    }

    private fun getGameLanguage(): Language {
        return if (preferencesGame.getString(Constants.GAME_LANGUAGE, "English")== "English") Language.ENGLISH
        else Language.FRENCH
    }
}