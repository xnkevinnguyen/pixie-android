package com.pixie.android.ui.draw.canvas

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.pixie.android.R
import com.pixie.android.data.game.ShowWordinGameType
import com.pixie.android.model.draw.CommandType
import com.pixie.android.type.GameMode
import com.pixie.android.type.GameStatus
import com.pixie.android.ui.chat.ChatViewModel
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils
import kotlinx.android.synthetic.main.canvas_fragment.*
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

class CanvasFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView: View = inflater.inflate(
            R.layout.canvas_fragment,
            container, false
        )


        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val factory = InjectorUtils.provideCanvasViewModelFactory()
        val canvasViewModel = ViewModelProvider(this, factory).get(CanvasViewModel::class.java)

        val chatFactory = InjectorUtils.provideChatViewModelFactory()
        val chatViewModel = ViewModelProvider(this, chatFactory).get(ChatViewModel::class.java)

        my_canvas.setViewModel(canvasViewModel)
        canvasViewModel.getPrimaryColor().observe(viewLifecycleOwner, Observer {
            my_canvas.drawColor = it.toArgb()
            my_canvas.reinitializeDrawingParameters()
        })

        canvasViewModel.getDrawCommandHistory().observe(viewLifecycleOwner, Observer {
            // should only draw DRAW commands and remove ERASE commands
            val filteredCommand = it.filter { it.value.type == CommandType.DRAW }

            val filteredPotraceComand = it.filter { it.value.type.equals(CommandType.DRAW_POTRACE) }
            if (!filteredPotraceComand.isNullOrEmpty()) {
                my_canvas.drawFromCommandListPotrace(ArrayList(filteredPotraceComand.values))

            } else if (!filteredCommand.isNullOrEmpty()) {
                my_canvas.drawFromCommandList(ArrayList(filteredCommand.values))

            } else {
                //both lists are empty
                my_canvas.drawFromCommandList(arrayListOf())
            }

        })

        canvasViewModel.getShouldShowWord().observe(viewLifecycleOwner, Observer {
            if (it.shouldShowWordBig && it.word != null) {
                if (it.type == ShowWordinGameType.IS_DRAWER)
                    display_word_top.text =
                        String.format(resources.getString(R.string.display_word_top), it.word)
                else if (it.type == ShowWordinGameType.OTHER_DRAWER)
                    display_word_top.text =
                        String.format(resources.getString(R.string.next_drawer), it.word)
                else if (it.type == ShowWordinGameType.ANSWER)
                    display_word_top.text =
                        String.format(resources.getString(R.string.answer_guess), it.word)
                display_word.visibility = View.VISIBLE

            } else if (!it.shouldShowWordBig && it.word != null) {
                display_word.visibility = View.GONE
                display_word_canvas.visibility = View.VISIBLE
                display_word_canvas.bringToFront()
                display_word_canvas.text =
                    String.format(resources.getString(R.string.your_word), it.word)
            } else {
                display_word.visibility = View.GONE
                display_word_canvas.visibility = View.GONE

            }
        })
        val preferencesSettings = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCES_SETTING, Context.MODE_PRIVATE)

        val soundOn:Boolean = preferencesSettings.getBoolean(Constants.NOTIFICATION, true)
        val mediaPlayer = chatViewModel.createMediaPlayer(R.raw.end_game, requireContext())


        canvasViewModel.getGameSession().observe(viewLifecycleOwner, Observer {
            if (it.status.equals(GameStatus.ENDED)) {


                display_end_game.visibility = View.VISIBLE


                val mode = display_end_game.findViewById<TextView>(R.id.mode_title)
                val img = display_end_game.findViewById<ImageView>(R.id.mode_picture)
                mode.text = it.mode.toString()
                if (it.mode == GameMode.SOLO)
                    img.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.ic_solo_mode
                        )
                    )
                else if (it.mode == GameMode.COOP)
                    img.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.ic_coop_mode
                        )
                    )
                else
                    img.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.ic_free_mode
                        )
                    )
                val rounds = display_end_game.findViewById<TextView>(R.id.number_round)
                val roundsString =
                    resources.getString(R.string.round_turn) + ": " + (it.currentRound.toInt() + 1).toString()
                rounds.text = roundsString


                val score = display_end_game.findViewById<TextView>(R.id.total_score)
                for (player in it.players) {
                    if (player.id == canvasViewModel.getUserID()
                    ) {
                        val scoreString =
                            resources.getString(R.string.total_score) + ": " + player.score.toString()
                        score.text = scoreString
                    }
                }
                val goToHome = display_end_game.findViewById<Button>(R.id.got_to_home)
                val gameData = it
                goToHome.setOnClickListener {
                    chatViewModel.exitChannel(gameData.channelID)
                    canvasViewModel.leaveGame()
                    val navController =
                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    navController.navigate(R.id.nav_game_selection)


                }

                val viewKonfetti = display_end_game.findViewById<KonfettiView>(R.id.viewKonfetti)
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

                if (soundOn) chatViewModel.startMediaPlayer(mediaPlayer)
                else chatViewModel.releaseMediaPlayer(mediaPlayer)


            } else {
                display_end_game.visibility = View.GONE
            }
        })

        canvasViewModel.getIsCanvasLocked().observe(viewLifecycleOwner, Observer {
            my_canvas.setIsCanvasLocked(it)
        })

        canvasViewModel.getStrokeWidth().observe(viewLifecycleOwner, Observer {
            my_canvas.drawStroke = it
            my_canvas.reinitializeDrawingParameters()
        })

        canvasViewModel.getCellWidthGrid().observe(viewLifecycleOwner, Observer {
            my_grid.setCellWidth(it)
        })

        canvasViewModel.getEraser().observe(viewLifecycleOwner, Observer {
            my_canvas.setErase(it)
        })

        canvasViewModel.getGridVal().observe(viewLifecycleOwner, Observer {
            showGrid(it)
        })

        super.onViewCreated(view, savedInstanceState)

    }


    private fun showGrid(gridOn: Boolean) {
        if (gridOn) {
            my_grid.visibility = View.VISIBLE
            my_canvas.setBackgroundColor(Color.TRANSPARENT)
        } else {
            my_grid.visibility = View.GONE
            my_canvas.setBackgroundColor(Color.WHITE)
        }
    }


}