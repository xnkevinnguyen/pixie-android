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

        val avatar : ImageView = root.findViewById(R.id.imageView)
        val lp = LinearLayout.LayoutParams(200, 200)
        lp.gravity = (Gravity.CENTER_HORIZONTAL)
        avatar.layoutParams =lp

        val header : LinearLayout = root.findViewById(R.id.header_profile)
        header.setPadding(16, 0, 16, 0)

        val username: TextView = root.findViewById(R.id.username)
        username.gravity = Gravity.CENTER_HORIZONTAL
        username.textSize = 25F
        username.text = preferences.getString(Constants.USERNAME, null)

        val name = root.findViewById<TextView>(R.id.name)
        val surname = root.findViewById<TextView>(R.id.surname)
        val id = root.findViewById<TextView>(R.id.user_id)
        val editBtn = root.findViewById<Button>(R.id.edit_user_info)

        val stringId = resources.getString(R.string.id) + " " + preferences.getString(Constants.USER_ID, null)
        id.text = stringId

        editBtn.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.edit_user_info)
            dialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
            val applyBtn = dialog.findViewById<Button>(R.id.btn_apply)
            applyBtn.setOnClickListener {
                Log.d("here", "apply")
                val stringName = resources.getString(R.string.name) + " " + dialog.findViewById<EditText>(R.id.name_edit).text.toString()
                name.text = stringName
                val stringSurname = resources.getString(R.string.surname) + " " + dialog.findViewById<EditText>(R.id.surname_edit).text.toString()
                surname.text = stringSurname

                dialog.dismiss()
            }
            dialog.show()
        }

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