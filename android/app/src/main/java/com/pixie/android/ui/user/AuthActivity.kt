package com.pixie.android.ui.user


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pixie.android.R
import com.pixie.android.utilities.Constants

class AuthActivity : AppCompatActivity() {

    private lateinit var preferencesSettings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        preferencesSettings = this.getSharedPreferences(Constants.SHARED_PREFERENCES_SETTING, Context.MODE_PRIVATE)
        val theme = preferencesSettings.getString(Constants.THEME, "Dark")
        if (theme == "Dark") setTheme(R.style.AppTheme)
        else setTheme(R.style.AppLightTheme)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.login_content_main)
    }
}

