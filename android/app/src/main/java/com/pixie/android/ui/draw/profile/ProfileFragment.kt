package com.pixie.android.ui.draw.profile

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.ui.draw.history.connectionHistory.ConnectionHistoryFragment
import com.pixie.android.ui.draw.history.gameHistory.GameHistoryFragment
import com.pixie.android.ui.user.AuthActivity
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils

class ProfileFragment: Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val factory = InjectorUtils.provideProfileViewModelFactory()
        profileViewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.profile_fragment, container, false)
        val logout: Button = root.findViewById(R.id.btn_logout)
        preferences = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCES_LOGIN, Context.MODE_PRIVATE)
        editor = preferences.edit()

        val intent = Intent(root.context, AuthActivity::class.java)

        logout.setOnClickListener {
            profileViewModel.logout()
            editor.remove("isLoggedIn")
            editor.apply()
            startActivity(intent)
            requireActivity().finish()
        }

        val avatar : ImageView = root.findViewById(R.id.avatar)
        val lp = LinearLayout.LayoutParams(200, 200)
        avatar.layoutParams =lp

        val username: TextView = root.findViewById(R.id.user_username)
        username.text = preferences.getString(Constants.USERNAME, null)

        val showHistory = root.findViewById<Button>(R.id.btn_show_hist)
        showHistory.setOnClickListener {
            val dialog = ConnectionHistoryFragment()
            dialog.show(childFragmentManager, "ConnectionDialogFragment")
        }

        val showGameHistory = root.findViewById<Button>(R.id.btn_game_hist)
        showGameHistory.setOnClickListener {
            val dialog = GameHistoryFragment()
            dialog.show(childFragmentManager, "GameDialogFragment")
        }


        return root
    }
}