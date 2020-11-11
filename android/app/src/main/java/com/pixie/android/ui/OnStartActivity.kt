package com.pixie.android.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.ui.user.AuthActivity
import com.pixie.android.ui.user.login.LoginViewModel
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils

class OnStartActivity:AppCompatActivity() {
    private lateinit var preferencesSettings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?){
        preferencesSettings = this.getSharedPreferences(Constants.SHARED_PREFERENCES_SETTING, Context.MODE_PRIVATE)
        val theme = preferencesSettings.getString(Constants.THEME, "Dark")
        if (theme == "Dark") setTheme(R.style.AppTheme)
        else if(theme == "Light") setTheme(R.style.AppLightTheme_NoActionBar)
        else if (theme == "Pink-Brown") setTheme(R.style.AppPinkTheme_NoActionBar)
        else if(theme == "Green-Gray") setTheme(R.style.AppGreenTheme_NoActionBar)
        else setTheme(R.style.AppBlueTheme_NoActionBar)

        super.onCreate(savedInstanceState)

        val preferences = applicationContext.getSharedPreferences(
            Constants.SHARED_PREFERENCES_LOGIN,
            Context.MODE_PRIVATE
        )
        val userIDPreference = preferences.getString(Constants.USER_ID, null)
        val usernamePreference = preferences.getString(Constants.USERNAME, null)
        if (preferences.getBoolean(Constants.SHARED_PREFERENCES_LOGIN_STATUS, false)
            && userIDPreference != null && usernamePreference != null
        ) {
            val mainIntent = Intent(applicationContext, MainActivity::class.java)
            startActivity(mainIntent)

            val factory = InjectorUtils.provideLoginViewModelFactory()
            val loginViewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)
            loginViewModel.userPreviousLogin(userIDPreference.toDouble(), usernamePreference)
            finish()
        }else{
            val loginIntent = Intent(applicationContext, AuthActivity::class.java)
            startActivity(loginIntent)
            finish()
        }

    }
}