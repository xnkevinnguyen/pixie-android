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
import com.pixie.android.type.Language
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


        preferencesSettings = requireContext().getSharedPreferences(
            Constants.SHARED_PREFERENCES_SETTING,
            Context.MODE_PRIVATE
        )
        editorSettings = preferencesSettings.edit()
        val appliedChange = preferencesSettings.getBoolean(Constants.APPLIEDCHANGE, true)

        val itemsLang = arrayOf(resources.getString(R.string.eng), resources.getString(R.string.fr))
        val adapterLang: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            R.layout.spinner_layout,
            itemsLang
        )

        val itemsTheme = arrayOf(
            resources.getString(R.string.dark),
            resources.getString(R.string.light),
            resources.getString(R.string.christmas),
            resources.getString(R.string.pink),
            resources.getString(R.string.halloween)
        )
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

        val savedLanguageValue: String?
        val savedThemeValue: String?

//        }
        if (langInMemory.equals("English") || langInMemory.equals("Anglais")) {
            if (!appliedChange) {
                dropdownLang.setSelection(adapterLang.getPosition(alwaysInEnglish(langInMemory)))
                savedLanguageValue = alwaysInEnglish(langInMemory)
                dropdownTheme.setSelection(adapterTheme.getPosition(alwaysInEnglish(themeInMemory)))
                savedThemeValue = alwaysInEnglish(themeInMemory)
            } else {
                savedLanguageValue = langInMemory
                savedThemeValue = themeInMemory
                dropdownLang.setSelection(adapterLang.getPosition(alwaysInEnglish(langInMemory)))
                dropdownTheme.setSelection(adapterTheme.getPosition(alwaysInEnglish(themeInMemory)))
            }
        } else {
            if (!appliedChange) {
                dropdownLang.setSelection(adapterLang.getPosition(alwaysInFrench(langInMemory)))
                savedLanguageValue = translateLanguageWord(alwaysInFrench(langInMemory))
                dropdownTheme.setSelection(adapterTheme.getPosition(alwaysInFrench(themeInMemory)))
                savedThemeValue = alwaysInFrench(themeInMemory)
            } else {
                savedLanguageValue = langInMemory
                savedThemeValue = themeInMemory
                dropdownLang.setSelection(adapterLang.getPosition(alwaysInFrench(langInMemory)))
                dropdownTheme.setSelection(adapterTheme.getPosition(alwaysInFrench(themeInMemory)))
            }
        }

        val apply = builder.findViewById<Button>(R.id.apply_settings)

        val notificationSound = builder.findViewById<SwitchCompat>(R.id.notification_switch)
        val soundOn: Boolean = preferencesSettings.getBoolean(Constants.NOTIFICATION, true)
        notificationSound.isChecked = soundOn

        apply.setOnClickListener {
            val themeValue = dropdownTheme.selectedItem.toString()
            val langValue = dropdownLang.selectedItem.toString()
            if (savedThemeValue != themeValue) applyThemeSettings(themeValue)
            applyLanguageSettings(langValue)
            if (notificationSound.isChecked) {
                editorSettings.putBoolean(Constants.NOTIFICATION, true)
                editorSettings.apply()
            } else {
                editorSettings.putBoolean(Constants.NOTIFICATION, false)
                editorSettings.apply()
            }
            val language =
                if (alwaysInEnglish(langValue).equals(Constants.LANGUAGE_FRENCH)) Language.FRENCH
                else Language.ENGLISH
            settingsViewModel.sendConfig(language, alwaysInEnglish(themeValue))

            builder.dismiss()
        }

        return builder
    }

    private fun alwaysInEnglish(value: String): String {
        val inputValue = if (value == requireContext().resources.getString(R.string.dark)) "Dark"
        else if (value == requireContext().resources.getString(R.string.light)) "Light"
        else if (value == requireContext().resources.getString(R.string.pink)) "Barbie"
        else if (value == requireContext().resources.getString(R.string.christmas)) "Christmas"
        else if (value == requireContext().resources.getString(R.string.halloween)) "Halloween"
        else if (value == requireContext().resources.getString(R.string.eng)) "English"
        else "French"
        return inputValue
    }

    private fun alwaysInFrench(value: String): String {
        val inputValue = if (value == requireContext().resources.getString(R.string.dark)) "Sombre"
        else if (value == requireContext().resources.getString(R.string.light)) "Clair"
        else if (value == requireContext().resources.getString(R.string.pink)) "Barbie"
        else if (value == requireContext().resources.getString(R.string.christmas)) "Noel"
        else if (value == requireContext().resources.getString(R.string.halloween)) "Halloween"
        else if (value == requireContext().resources.getString(R.string.eng)) "Anglais"
        else if (value == "Dark") "Sombre"
        else if(value =="Light") "Clair"
        else if(value=="Barbie")"Barbie"
        else if(value=="Christmas") "Noel"
        else "Français"
        return inputValue
    }

    private fun applyThemeSettings(themeValue: String) {
        // Forcing value in preferences to always be in English and not change because of the language change
        val inputValue =
            if (themeValue == requireContext().resources.getString(R.string.dark)) "Dark"
            else if (themeValue == requireContext().resources.getString(R.string.light)) "Light"
            else if (themeValue == requireContext().resources.getString(R.string.pink)) "Barbie"
            else if (themeValue == requireContext().resources.getString(R.string.christmas)) "Christmas"
            else "Halloween"

        editorSettings.putString(Constants.THEME, inputValue)
        editorSettings.apply()

        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity()
    }

    private fun applyLanguageSettings(langValue: String) {
        // Forcing value in preferences to always be in English and not change because of the language change
        val inputValue =
            if (langValue == requireContext().resources.getString(R.string.eng)) "English"
            else "French"

        val appliedChange = preferencesSettings.getBoolean(Constants.APPLIEDCHANGE, true)
        editorSettings.putString(Constants.LANGUAGE, inputValue)
        editorSettings.putBoolean(Constants.APPLIEDCHANGE, !appliedChange)
        editorSettings.apply()

        val intent = Intent(requireContext(), MainActivity::class.java)

        startActivity(intent)
        requireActivity()
    }

    private fun translateLanguageWord(word: String?): String {
        return if (word == "French") "Français"
        else "Anglais"
    }

    private fun translateThemeWord(word: String?): String {
        return if (word == "Dark") "Sombre"
        else "Clair"

    }

}