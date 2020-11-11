package com.pixie.android.ui.draw.settings

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.ui.MainActivity
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils
import kotlin.properties.Delegates

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
        val appliedChange = preferencesSettings.getBoolean(Constants.APPLIEDCHANGE, true)

        val itemsLang = arrayOf(resources.getString(R.string.eng), resources.getString(R.string.fr))
        val adapterLang: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            R.layout.spinner_layout,
            itemsLang
        )

        val itemsTheme = arrayOf(resources.getString(R.string.dark), resources.getString(R.string.light), resources.getString(R.string.green),
            resources.getString(R.string.pink), resources.getString(R.string.blue))
        val adapterTheme: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            R.layout.spinner_layout,
            itemsTheme
        )

        val dropdownLang = builder.findViewById<Spinner>(R.id.language_spinner)
        dropdownLang.adapter = adapterLang
        val langInMemory = preferencesSettings.getString(Constants.LANGUAGE, "English")

        val dropdownTheme = builder.findViewById<Spinner>(R.id.spinner_theme)
        dropdownTheme.adapter = adapterTheme
        val themeInMemory = preferencesSettings.getString(Constants.THEME, "Dark")

        val savedLanguageValue:String?
        val savedThemeValue:String?
        if(!appliedChange){
            dropdownLang.setSelection(adapterLang.getPosition(translateLanguageWord(langInMemory)))
            savedLanguageValue = translateLanguageWord(langInMemory)
            dropdownTheme.setSelection(adapterTheme.getPosition(translateThemeWord(themeInMemory)))
            savedThemeValue = translateThemeWord(themeInMemory)
        }
        else {
            savedLanguageValue = langInMemory
            savedThemeValue = themeInMemory
            dropdownLang.setSelection(adapterLang.getPosition(langInMemory))
            dropdownTheme.setSelection(adapterTheme.getPosition(themeInMemory))
        }

        val apply = builder.findViewById<Button>(R.id.apply_settings)

        val notificationSound = builder.findViewById<SwitchCompat>(R.id.notification_switch)
        val soundOn:Boolean = preferencesSettings.getBoolean(Constants.NOTIFICATION, true)
        notificationSound.isChecked = soundOn

        apply.setOnClickListener {
            val themeValue = dropdownTheme.selectedItem.toString()
            val langValue = dropdownLang.selectedItem.toString()
            if(savedThemeValue != themeValue) applyThemeSettings(themeValue)
            if(langValue != savedLanguageValue) applyLanguageSettings(langValue)
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
        // Forcing value in preferences to always be in English and not change because of the language change
        val inputValue = if(themeValue == requireContext().resources.getString(R.string.dark)) "Dark"
        else if(themeValue == requireContext().resources.getString(R.string.light)) "Light"
        else if(themeValue == requireContext().resources.getString(R.string.pink)) "Pink-Brown"
        else if(themeValue == requireContext().resources.getString(R.string.green)) "Green-Grey"
        else "Blue"

        editorSettings.putString(Constants.THEME, inputValue)
        editorSettings.apply()

        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity()
    }

    private fun applyLanguageSettings(langValue:String){
        // Forcing value in preferences to always be in English and not change because of the language change
        val inputValue = if(langValue == requireContext().resources.getString(R.string.eng)) "English"
        else "French"

        val appliedChange = preferencesSettings.getBoolean(Constants.APPLIEDCHANGE, true)
        editorSettings.putString(Constants.LANGUAGE, inputValue)
        editorSettings.putBoolean(Constants.APPLIEDCHANGE, !appliedChange)
        editorSettings.apply()

        val intent = Intent(requireContext(), MainActivity::class.java)

        startActivity(intent)
        requireActivity()
    }

    private fun translateLanguageWord(word: String?):String {
        return if(word == "French") "Français"
        else "Anglais"
    }

    private fun translateThemeWord(word: String?):String {
        return if(word == "Dark") "Sombre"
        else "Clair"

    }

}