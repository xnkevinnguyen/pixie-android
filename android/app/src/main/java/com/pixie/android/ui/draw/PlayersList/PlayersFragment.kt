package com.pixie.android.ui.draw.ChannelList

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.ui.chat.ChannelParticipantAdapter
import com.pixie.android.utilities.InjectorUtils


class PlayersFragment: Fragment() {

    private lateinit var playersViewModel: PlayersViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val factory = InjectorUtils.providePlayersViewModelFactory()
        playersViewModel = ViewModelProvider(this, factory).get(PlayersViewModel::class.java)

        val root = inflater.inflate(R.layout.player_list_fragment, container, false)

        val search = root.findViewById<EditText>(R.id.search_player)
        val participantListElement = root.findViewById<ListView>(R.id.participants)
        val participantAdapter = ChannelParticipantAdapter(requireContext())
        participantListElement.adapter = participantAdapter

        val mainChannelParticipantList = playersViewModel.getMainChannelParticipants()
        mainChannelParticipantList.observe(viewLifecycleOwner, Observer { participantList->
            if(!participantList.isNullOrEmpty()){
                participantAdapter.clear()
                participantList.forEach{
                    participantAdapter.add(it)
                }

            }

        })

        participantListElement.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(adapterView: AdapterView<*>?, childView: View?, position: Int, id: Long) {
                val user: ChannelParticipant = participantListElement.getItemAtPosition(position) as ChannelParticipant
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.other_user_info)
                dialog.findViewById<TextView>(R.id.user_name).text = user.username
                dialog.show()
            }
        }

//        search.setOnKeyListener(object : View.OnKeyListener {
//            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
//                // If the event is a key-down event on the "enter" button
//                if (event.getAction() === KeyEvent.ACTION_DOWN &&
//                    keyCode == KeyEvent.KEYCODE_ENTER
//                ) {
//                    val user = search.text.toString()
//                    return true
//                }
//                return false
//            }
//        })

        search.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                println("Text [$s]")
                participantAdapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {}
        })
        return root
    }


}