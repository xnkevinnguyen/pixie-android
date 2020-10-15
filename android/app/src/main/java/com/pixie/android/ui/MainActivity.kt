package com.pixie.android.ui


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.pixie.android.R
import com.pixie.android.data.user.UserRepository
import com.pixie.android.ui.chat.ChatViewModel
import com.pixie.android.ui.draw.profile.ProfileViewModel
import com.pixie.android.ui.draw.settings.SettingsFragment
import com.pixie.android.ui.user.AuthActivity
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils
import com.pixie.android.utilities.OnApplicationStopService
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.coroutines.runBlocking


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var preferencesSettings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        preferencesSettings = this.getSharedPreferences(Constants.SHARED_PREFERENCES_SETTING, Context.MODE_PRIVATE)
        val theme = preferencesSettings.getString(Constants.THEME, "Dark")
        if (theme == "Dark") setTheme(R.style.AppTheme_NoActionBar)
        else setTheme(R.style.AppLightTheme_NoActionBar)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        startService(Intent(baseContext, OnApplicationStopService::class.java))

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_home, R.id.nav_drawing, R.id.nav_game_selection), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val header: View = navView.getHeaderView(0)
        val avatar: ImageView = header.findViewById(R.id.imageView)
        avatar.setOnClickListener {
//            val factory = InjectorUtils.provideProfileViewModelFactory()
//            val profileViewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)
//            profileViewModel.fetchUserInfo()
            navController.navigate(R.id.nav_profile)
            drawerLayout.closeDrawer(GravityCompat.START, false)
        }

        val preferences = this.getSharedPreferences(Constants.SHARED_PREFERENCES_LOGIN, Context.MODE_PRIVATE)
        val usernamePreference = preferences.getString(Constants.USERNAME, null)
        val username = header.findViewById<TextView>(R.id.username)
        username.text = usernamePreference


        settings.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START, false)
            val dialog = SettingsFragment()
            dialog.show(supportFragmentManager, "SettingsDialogFragment")
        }

        tutorial.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.tutorial_layout)
            dialog.show()
        }


    }

    override fun onPostCreate(savedInstanceState: Bundle?) {

        //Start channel subscriptions
        val factory = InjectorUtils.provideChatViewModelFactory()
        val chatViewModel = ViewModelProvider(this, factory).get(ChatViewModel::class.java)
        chatViewModel.startChannelsIfNecessary()
        super.onPostCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Added 3 dots in the right up corner
        menuInflater.inflate(R.menu.profile_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val profileFactory = InjectorUtils.provideProfileViewModelFactory()
        val profileViewModel = ViewModelProvider(this, profileFactory).get(ProfileViewModel::class.java)
        val preferences = this.getSharedPreferences(Constants.SHARED_PREFERENCES_LOGIN, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val intent = Intent(this, AuthActivity::class.java)

        return when (item.itemId) {
            R.id.action_dropdown1 ->{
                val navController = findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.nav_profile)
                return true
            }
            R.id.action_dropdown2 ->{
                profileViewModel.logout()
                editor.remove("isLoggedIn")
                editor.apply()
                startActivity(intent)
                this.finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    // Disable back press since drawing undo-redo crashes if back pressed to drawing fragment
    override fun onBackPressed() {
        return
    }
    override fun onDestroy() {
        // stop channel subscriptions
        val factory = InjectorUtils.provideChatViewModelFactory()
        val chatViewModel = ViewModelProvider(this, factory).get(ChatViewModel::class.java)
        runBlocking {
            chatViewModel.stopChannel()
            UserRepository.getInstance().logout()
        }

        super.onDestroy()
    }

}