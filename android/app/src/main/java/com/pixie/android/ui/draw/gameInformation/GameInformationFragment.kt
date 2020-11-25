package com.pixie.android.ui.draw.gameInformation

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.pixie.android.R
import com.pixie.android.type.GameMode
import com.pixie.android.type.GameStatus
import com.pixie.android.ui.chat.ChatViewModel
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size


class GameInformationFragment : Fragment() {

    private lateinit var preferencesUser: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.game_information_fragment, container, false)
        val factory = InjectorUtils.provideGameInformationViewModelFactory()
        preferencesUser = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCES_LOGIN, Context.MODE_PRIVATE)

        val gameInfoViewModel =
            ViewModelProvider(this, factory).get(GameInformationViewModel::class.java)

        val chatFactory = InjectorUtils.provideChatViewModelFactory()
        val chatViewModel = ViewModelProvider(this, chatFactory).get(ChatViewModel::class.java)

        val timerElement = root.findViewById<TextView>(R.id.time_left)
        val guessLeftElement = root.findViewById<TextView>(R.id.number_guess_left)
        val mode = root.findViewById<TextView>(R.id.mode_of_game)
        val round = root.findViewById<TextView>(R.id.round_number)
        val listPlayer = root.findViewById<ListView>(R.id.players_in_game)
        val leaveGameBtn = root.findViewById<Button>(R.id.leave_game)

        val gameID = gameInfoViewModel.getGameSession().value?.id
        val channelID = gameInfoViewModel.getGameSession().value?.channelID


        leaveGameBtn.setOnClickListener {
            gameInfoViewModel.leaveGame()

            if (channelID != null) {
                chatViewModel.exitChannel(channelID)
            }
            if (gameID != null) {
                chatViewModel.exitGame(gameID)
            }

            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_home)
        }

        guessLeftElement.text =
            gameInfoViewModel.getGameSession().value?.guessesLeft?.toInt().toString()

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    gameInfoViewModel.leaveGame()

                    if (channelID != null) {
                        chatViewModel.exitChannel(channelID)
                    }
                    if (gameID != null) {
                        chatViewModel.exitGame(gameID)
                    }

                    val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.nav_home)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        val listPlayerAdapter = ListPlayerAdapter(requireContext())

        listPlayer.adapter = listPlayerAdapter


        gameInfoViewModel.getTimer().observe(viewLifecycleOwner, Observer {
            timerElement.text = it.toString()
        })

        val preferencesSettings = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCES_SETTING, Context.MODE_PRIVATE)
        val soundOn:Boolean = preferencesSettings.getBoolean(Constants.NOTIFICATION, true)
        val mediaPlayer = chatViewModel.createMediaPlayer(R.raw.end_game, requireContext())

        gameInfoViewModel.getGameSession().observe(viewLifecycleOwner, Observer {
            var roundString: String =
                (it.currentRound.toInt() + 1).toString()
            if(it.mode == GameMode.FREEFORALL){
                roundString = (it.currentRound.toInt() + 1).toString() + "/3"
            }

            round.text = roundString
            guessLeftElement.text = it.guessesLeft?.toInt().toString()
            listPlayerAdapter.set(it.players)
            listPlayerAdapter.setDrawer(it.currentDrawerId)
            if (it.status.equals(GameStatus.ENDED)) {

                chatViewModel.exitChannel(it.channelID)
                gameInfoViewModel.leaveGame()

                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.game_end_result)
                //can't close dialog if click back press or exterior of window
                dialog.setCancelable(false)
                dialog.setCanceledOnTouchOutside(false)

                val mode = dialog.findViewById<TextView>(R.id.mode_title)
                val img = dialog.findViewById<ImageView>(R.id.mode_picture)
                mode.text = it.mode.toString()
                if(it.mode == GameMode.SOLO)
                    img.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_solo_mode))
                else if(it.mode == GameMode.COOP)
                    img.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_coop_mode))
                else
                    img.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_free_mode))

                val score = dialog.findViewById<TextView>(R.id.total_score)
                for(player in it.players) {
                    if(player.id.toString() == preferencesUser.getString(Constants.USER_ID, null)) {
                        val scoreString =
                            resources.getString(R.string.total_score) + ": " + player.score.toString()
                        score.text = scoreString
                    }
                }

                val rounds = dialog.findViewById<TextView>(R.id.number_round)
                val roundsString = resources.getString(R.string.round_turn) + ": " + (it.currentRound.toInt() + 1).toString()
                rounds.text = roundsString


                val goToHome = dialog.findViewById<Button>(R.id.got_to_home)
                goToHome.setOnClickListener {
                    val navController =
                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    navController.navigate(R.id.nav_home)
                    dialog.dismiss()
                }

                val viewKonfetti = dialog.findViewById<KonfettiView>(R.id.viewKonfetti)
                viewKonfetti.build()
                    .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                    .setDirection(0.0, 359.0)
                    .setSpeed(1f, 5f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(2000L)
                    .addShapes(Shape.Square, Shape.Circle)
                    .addSizes(Size(12))
                    .setPosition(-50f, 850 + 50f, -50f, -50f)
                    .streamFor(300, 5000L)

                dialog.show()

                if(soundOn)chatViewModel.startMediaPlayer(mediaPlayer)
                else chatViewModel.releaseMediaPlayer(mediaPlayer)
            }
        })


        mode.text = gameInfoViewModel.getGameSession().value?.mode.toString()

        return root
    }



}