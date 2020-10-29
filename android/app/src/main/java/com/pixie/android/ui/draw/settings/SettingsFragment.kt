package com.pixie.android.ui.draw.settings

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
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
import java.util.*

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


//        val itemsLang = arrayOf(resources.getString(R.string.eng), resources.getString(R.string.fr))
//        val adapterLang: ArrayAdapter<String> = ArrayAdapter(
//            requireContext(),
//            R.layout.spinner_layout,
//            itemsLang
//        )

        val itemsTheme = arrayOf(resources.getString(R.string.dark), resources.getString(R.string.light))
        val adapterTheme: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            R.layout.spinner_layout,
            itemsTheme
        )

//        val dropdownLang = builder.findViewById<Spinner>(R.id.spinner_language)
//        dropdownLang.adapter = adapterLang
//        val langInMemory = preferencesSettings.getString(Constants.LANGUAGE, resources.getString(R.string.eng))
//        dropdownLang.setSelection(adapterLang.getPosition(langInMemory))

        val dropdownTheme = builder.findViewById<Spinner>(R.id.spinner_theme)
        dropdownTheme.adapter = adapterTheme
        val themeInMemory = preferencesSettings.getString(Constants.THEME, resources.getString(R.string.dark))
        dropdownTheme.setSelection(adapterTheme.getPosition(themeInMemory))

        val apply = builder.findViewById<Button>(R.id.apply_settings)

        val notificationSound = builder.findViewById<SwitchCompat>(R.id.notification_switch)
        val soundOn:Boolean = preferencesSettings.getBoolean(Constants.NOTIFICATION, true)
        notificationSound.isChecked = soundOn

        apply.setOnClickListener {
            val themeValue = dropdownTheme.selectedItem.toString()
            //val langValue = dropdownLang.selectedItem.toString()
            applyThemeSettings(themeValue)
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
        val themeSaved = preferencesSettings.getString(Constants.THEME, resources.getString(R.string.dark))

        if (themeSaved != themeValue){
            editorSettings.putString(Constants.THEME, themeValue)
            editorSettings.apply()

            val intent = Intent(requireContext(), MainActivity::class.java)

            startActivity(intent)
            requireActivity()
        }
    }
}