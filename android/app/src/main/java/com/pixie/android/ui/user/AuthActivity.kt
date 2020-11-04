package com.pixie.android.ui.user


import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.pixie.android.R
import com.pixie.android.ui.draw.settings.MyContextWrapper
import com.pixie.android.utilities.Constants
import java.util.*

class AuthActivity : AppCompatActivity() {

    private lateinit var preferencesSettings: SharedPreferences
    override fun attachBaseContext(newBase: Context) {
        preferencesSettings = newBase.getSharedPreferences(Constants.SHARED_PREFERENCES_SETTING, Context.MODE_PRIVATE)
        val lang = preferencesSettings.getString(Constants.LANGUAGE, "English")
        var languageAc = "en"
        val langValue = "French"
        languageAc = if(lang == langValue) "fr"
        else "en"
        super.attachBaseContext(MyContextWrapper(newBase).wrap(newBase,languageAc))
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration) {
        preferencesSettings = this.getSharedPreferences(Constants.SHARED_PREFERENCES_SETTING, Context.MODE_PRIVATE)
        val lang = preferencesSettings.getString(Constants.LANGUAGE, "English")
        var languageAc = "en"
        val langValue = "French"
        languageAc = if(lang == langValue) "fr"
        else "en"

        val locale = Locale(languageAc)
        overrideConfiguration.setLocale(locale)
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        preferencesSettings = this.getSharedPreferences(Constants.SHARED_PREFERENCES_SETTING, Context.MODE_PRIVATE)
        val theme = preferencesSettings.getString(Constants.THEME, "Dark")
        if (theme == "Dark") setTheme(R.style.AppTheme)
        else setTheme(R.style.AppLightTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_content_main)
    }
}

