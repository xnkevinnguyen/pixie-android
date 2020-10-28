package com.pixie.android.ui.draw.settings

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.model.user.LoggedInUser
import com.pixie.android.ui.MainActivity
import com.pixie.android.ui.draw.history.connectionHistory.ConnectionAdapter
import com.pixie.android.ui.draw.home.HomeViewModel
import com.pixie.android.ui.user.login.LoginViewModel
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils

class SettingsFragment : DialogFragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var preferencesSettings: SharedPreferences
    private lateinit var editorSettings: SharedPreferences.Editor

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val factory = InjectorUtils.provideSettingsViewModelFactory()
        settingsViewModel = ViewModelProvider(this, factory).get(SettingsViewModel::class.java)

        val builder = Dialog(requireContext())
        val inflater = requireActivity().layoutInflater
        val layout: View = inflater.inflate(R.layout.settings_fragment, null)
        builder.setContentView(layout)

        preferencesSettings = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCES_SETTING, Context.MODE_PRIVATE)
        editorSettings = preferencesSettings.edit()


        val itemsLang = arrayOf(resources.getString(R.string.eng), resources.getString(R.string.fr))
        val adapterLang: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            R.layout.spinner_layout,
            itemsLang
        )

        val itemsTheme = arrayOf(resources.getString(R.string.dark), resources.getString(R.string.light))
        val adapterTheme: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            R.layout.spinner_layout,
            itemsTheme
        )

        val dropdownLang = builder.findViewById<Spinner>(R.id.spinner_language)
        dropdownLang.adapter = adapterLang
        val langInMemory = preferencesSettings.getString(Constants.LANGUAGE, "English")
        dropdownLang.setSelection(adapterLang.getPosition(langInMemory))

        val dropdownTheme = builder.findViewById<Spinner>(R.id.spinner_theme)
        dropdownTheme.adapter = adapterTheme
        val themeInMemory = preferencesSettings.getString(Constants.THEME, "Dark")
        dropdownTheme.setSelection(adapterTheme.getPosition(themeInMemory))

        val apply = builder.findViewById<Button>(R.id.apply_settings)

        val notificationSound = builder.findViewById<SwitchCompat>(R.id.notification_switch)
        val soundOn:Boolean = preferencesSettings.getBoolean(Constants.NOTIFICATION, true)
        notificationSound.isChecked = soundOn

        apply.setOnClickListener {
            val themeValue = dropdownTheme.selectedItem.toString()
            val langValue = dropdownLang.selectedItem.toString()
            applyThemeSettings(themeValue)
            applyLanguageSettings(langValue)
            if(notificationSound.isChecked) {
                editorSettings.putBoolean(Constants.NOTIFICATION, true)
                editorSettings.apply()
            }
            else {
                editorSettings.putBoolean(Constants.NOTIFICATION, false)
                editorSettings.apply()
            }
            builder.dismiss()
        }



        return builder
    }

    private fun applyThemeSettings(themeValue:String){
        val themeSaved = preferencesSettings.getString(Constants.THEME, "Dark")

        if (themeSaved != themeValue){
            editorSettings.putString(Constants.THEME, themeValue)
            editorSettings.apply()

            val intent = Intent(requireContext(), MainActivity::class.java)

            startActivity(intent)
            requireActivity()

//            val dialog = Dialog(requireContext())
//            dialog.setContentView(R.layout.warning_change_theme_language)
//            val closeBtn = dialog.findViewById<ImageView>(R.id.close)
//            closeBtn.setOnClickListener {
//                dialog.dismiss()
//            }
//            dialog.show()
        }
    }

    private fun applyLanguageSettings(langValue:String){
        val langSaved = preferencesSettings.getString(Constants.LANGUAGE, "English")
        if (langSaved != langValue){
            editorSettings.putString(Constants.LANGUAGE, langValue)
            editorSettings.apply()
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.warning_change_theme_language)
            val closeBtn = dialog.findViewById<ImageView>(R.id.close)
            closeBtn.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }
}