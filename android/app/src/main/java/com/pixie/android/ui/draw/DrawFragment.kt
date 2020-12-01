package com.pixie.android.ui.draw

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.pixie.android.R
import com.pixie.android.type.GameStatus
import com.pixie.android.ui.chat.ChatViewModel
import com.pixie.android.ui.draw.gameInformation.GameInformationViewModel
import com.pixie.android.ui.draw.profile.ProfileViewModel
import com.pixie.android.ui.user.AuthActivity
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils

class DrawFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val root = inflater.inflate(
            R.layout.draw_fragment,
            container, false
        )

        val gameInfoFactory = InjectorUtils.provideGameInformationViewModelFactory()

        val gameInfoViewModel =
            ViewModelProvider(this, gameInfoFactory).get(GameInformationViewModel::class.java)

        val tools = root.findViewById<LinearLayout>(R.id.tools_layout)

        gameInfoViewModel.getGameSession().observe(viewLifecycleOwner, Observer {
            val isUserTheDrawer = gameInfoViewModel.isUserTheDrawer(it.currentDrawerId)

            if(isUserTheDrawer ) {
                tools.visibility = View.VISIBLE
            } else {
                tools.visibility = View.GONE
            }

            if(it.status == GameStatus.ENDED){
                tools.visibility = View.GONE
            }
        })

        return root
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val factory = InjectorUtils.provideGameInformationViewModelFactory()
//        val gameInfoViewModel = ViewModelProvider(this,factory).get(GameInformationViewModel::class.java)
//
//        val chatFactory = InjectorUtils.provideChatViewModelFactory()
//        val chatViewModel = ViewModelProvider(this, chatFactory).get(ChatViewModel::class.java)
//
//        return when (item.itemId) {
//            android.R.id.home ->{
//                val gameID = gameInfoViewModel.getGameSession().value?.id
//                val channelID = gameInfoViewModel.getGameSession().value?.channelID
//                if (channelID != null) {
//                    chatViewModel.exitChannel(channelID)
//                }
//                if (gameID != null) {
//                    chatViewModel.exitGame(gameID)
//                }
//                val navController = requireActivity().findNavController(R.id.nav_host_fragment)
//                navController.navigate(R.id.nav_home)
//                return true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }


}