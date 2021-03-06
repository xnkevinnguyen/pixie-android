package com.pixie.android.ui


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import com.pixie.android.R
import com.pixie.android.data.user.UserRepository
import com.pixie.android.type.GameDifficulty
import com.pixie.android.type.GameMode
import com.pixie.android.type.GameState
import com.pixie.android.type.GameStatus
import com.pixie.android.ui.chat.ChatViewModel
import com.pixie.android.ui.draw.gameInformation.GameInformationViewModel
import com.pixie.android.ui.draw.profile.ProfileViewModel
import com.pixie.android.ui.draw.settings.MyContextWrapper
import com.pixie.android.ui.draw.settings.SettingsFragment
import com.pixie.android.ui.user.AuthActivity
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils
import com.pixie.android.utilities.OnApplicationStopService
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var preferencesSettings: SharedPreferences
    private lateinit var preferencesLogin: SharedPreferences
    private lateinit var profileViewModel: ProfileViewModel

    override fun attachBaseContext(newBase: Context) {
        preferencesSettings = newBase.getSharedPreferences(
            Constants.SHARED_PREFERENCES_SETTING,
            Context.MODE_PRIVATE
        )
        val lang = preferencesSettings.getString(Constants.LANGUAGE, "English")
        var languageAc = "en"
        val langValue = "French"
        languageAc = if (lang == langValue) "fr"
        else "en"
        Log.d("MainActivity", "Load language")

        super.attachBaseContext(MyContextWrapper(newBase).wrap(newBase, languageAc))
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration) {
        preferencesSettings = this.getSharedPreferences(
            Constants.SHARED_PREFERENCES_SETTING,
            Context.MODE_PRIVATE
        )
        val lang = preferencesSettings.getString(Constants.LANGUAGE, "English")
        var languageAc = "en"
        val langValue = "French"
        languageAc = if (lang == langValue) "fr"
        else "en"

        val locale = Locale(languageAc)
        overrideConfiguration.setLocale(locale)
        Log.d("MainActivity", "Load language")
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        preferencesSettings = this.getSharedPreferences(
            Constants.SHARED_PREFERENCES_SETTING,
            Context.MODE_PRIVATE
        )
        val theme = preferencesSettings.getString(Constants.THEME, "Dark")
        if (theme == "Halloween") setTheme(R.style.AppBlueTheme_NoActionBar)
        else if (theme == "Light") setTheme(R.style.AppLightTheme_NoActionBar)
        else if (theme == resources.getString(R.string.pink)) setTheme(R.style.AppPinkTheme_NoActionBar)
        else if (theme == "Christmas") setTheme(R.style.AppGreenTheme_NoActionBar)
        else setTheme(R.style.AppTheme_NoActionBar)

        val profileFactory = InjectorUtils.provideProfileViewModelFactory()
        profileViewModel = ViewModelProvider(this, profileFactory).get(ProfileViewModel::class.java)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        startService(Intent(baseContext, OnApplicationStopService::class.java))

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_game_selection,
                R.id.nav_chat,
                R.id.nav_profile,
                R.id.nav_leaderboard
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val header: View = navView.getHeaderView(0)
        val avatar: ImageView = header.findViewById(R.id.imageView)

        val colors = profileViewModel.getAvatarColor()
        var foregroundColorInt: Int? = null
        if (!colors.foreground.isNullOrEmpty()) {
            foregroundColorInt = Color.parseColor(colors.foreground)
        }
        if (foregroundColorInt == null) {
            foregroundColorInt =
                Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
        }


        var backgroundColorInt: Int? = null
        if (!colors.background.isNullOrEmpty()) {
            backgroundColorInt = Color.parseColor(colors.background)
        }
        if (backgroundColorInt == null) {
            backgroundColorInt =
                Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
        }
        avatar.setColorFilter(
            backgroundColorInt
        )

        avatar.backgroundTintList = ColorStateList.valueOf(
            foregroundColorInt
        )

        avatar.setOnClickListener {
            navController.navigate(R.id.nav_profile)
            drawerLayout.closeDrawer(GravityCompat.START, false)
        }

        val preferences = this.getSharedPreferences(
            Constants.SHARED_PREFERENCES_LOGIN,
            Context.MODE_PRIVATE
        )
        val usernamePreference = preferences.getString(Constants.USERNAME, null)
        val username = header.findViewById<TextView>(R.id.username)
        username.text = usernamePreference


        settings.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START, false)
            val dialog = SettingsFragment()
            dialog.show(supportFragmentManager, "SettingsDialogFragment")
        }

        tutorial.setOnClickListener {
            navController.navigate(R.id.nav_tutorial)
            drawerLayout.closeDrawer(GravityCompat.START, false)
        }

        // handle new user
        val params = intent.extras
        var value = -1 // or other values
        if (params != null) value = params.getInt("isNewUser")

        if (value > 0 && params != null) {
            params.putInt("isNewUser", -1); //Your id
            intent.putExtras(params)
            navController.navigate(R.id.nav_tutorial)

        }


    }

    override fun onPostCreate(savedInstanceState: Bundle?) {

        //Start channel subscriptions
        val factory = InjectorUtils.provideChatViewModelFactory()
        val chatViewModel = ViewModelProvider(this, factory).get(ChatViewModel::class.java)
        chatViewModel.startChannelsIfNecessary()

        //handles if someone else started the game
        val gameSession = chatViewModel.getGameSession()
        gameSession.observe(this, androidx.lifecycle.Observer {
            val gameState = it.state
            Log.d("StartGameEvent", it.status.toString() + " " + it.state.toString())
            if (it.status == GameStatus.STARTED && gameState != null && (gameState == GameState.DRAWER_SELECTION || gameState == GameState.START)) {

                val navController =
                    Navigation.findNavController(this, R.id.nav_host_fragment)
                navController.navigate(R.id.nav_drawing)


            }
        })
        chatViewModel.subscribeToGameInvitation()

        chatViewModel.getGameInvitation()
            .observe(this, androidx.lifecycle.Observer { gameInvitation ->
                if (!chatViewModel.getHasGameInvitationBeenShown()) {
                    val dialog = Dialog(this)
                    dialog.setContentView(R.layout.game_invitation)
                    val acceptButtonElement = dialog.findViewById<Button>(R.id.accept_invitation)
                    val declineButtonElement = dialog.findViewById<Button>(R.id.decline_invitation)

                    acceptButtonElement.setOnClickListener {
                        val accept = chatViewModel.acceptInvitation(gameInvitation.gameID)

                        dialog.hide()
                        if (accept == null) {
                            Toast.makeText(
                                this,
                                resources.getString(R.string.game_invite_error),
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            val navController =
                                Navigation.findNavController(this, R.id.nav_host_fragment)
                            navController.navigate(R.id.nav_chat)
                        }
                    }
                    declineButtonElement.setOnClickListener {
                        dialog.hide()
                    }
                    val invitationText = dialog.findViewById<TextView>(R.id.invite_title)
                    invitationText.text = String.format(
                        resources.getString(R.string.game_invitation_title),
                        gameInvitation.sender.username
                    )

                    val mode = dialog.findViewById<TextView>(R.id.mode_title)
                    val img = dialog.findViewById<ImageView>(R.id.mode_picture)
                    mode.text = gameInvitation.gameMode.toString()

                    val languageElement = dialog.findViewById<ImageView>(R.id.language_icon)

                    if (gameInvitation.language.rawValue == "ENGLISH") {
                        languageElement.setImageDrawable(this.let {
                            ContextCompat.getDrawable(
                                it,
                                R.drawable.ic_uk_flag
                            )
                        })
                    } else {
                        languageElement.setImageDrawable(this.let {
                            ContextCompat.getDrawable(
                                it,
                                R.drawable.ic_flag_of_france
                            )
                        })
                    }
                    val difficultyElement = dialog.findViewById<ImageView>(R.id.difficulty_icon)
                    if (gameInvitation.difficulty == GameDifficulty.EASY) {
                        difficultyElement.setImageDrawable(this.let {
                            ContextCompat.getDrawable(
                                it,
                                R.drawable.ic_easy_diff
                            )
                        })
                        difficultyElement.setColorFilter(Color.GREEN)
                    } else if (gameInvitation.difficulty == GameDifficulty.MEDIUM) {
                        difficultyElement.setImageDrawable(this.let {
                            ContextCompat.getDrawable(
                                it,
                                R.drawable.ic_medium_diff
                            )
                        })
                        difficultyElement.setColorFilter(Color.YELLOW)
                    } else {
                        difficultyElement.setImageDrawable(this.let {
                            ContextCompat.getDrawable(
                                it,
                                R.drawable.ic_hard_diff
                            )
                        })
                        difficultyElement.setColorFilter(Color.RED)
                    }

                    if (gameInvitation.gameMode == GameMode.SOLO)
                        img.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.ic_solo_mode
                            )
                        )
                    else if (gameInvitation.gameMode == GameMode.COOP)
                        img.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.ic_coop_mode
                            )
                        )
                    else
                        img.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.ic_free_mode
                            )
                        )


                    dialog.show()
                    chatViewModel.confirmInvitationBeenShown()
                }

            })
        super.onPostCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Added 3 dots in the right up corner
        menuInflater.inflate(R.menu.profile_menu, menu)


        val colors = profileViewModel.getAvatarColor()
        var foregroundColor: Int? = null
        if (!colors.foreground.isNullOrEmpty()) {
            foregroundColor = Color.parseColor(colors.foreground)
        }
        if (foregroundColor == null) {
            foregroundColor =
                Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
        }

        var backgroundColor: Int? = null
        if (!colors.background.isNullOrEmpty()) {
            backgroundColor = Color.parseColor(colors.background)
        }
        if (backgroundColor == null) {
            backgroundColor =
                Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
        }

        val drawable =
            ContextCompat.getDrawable(applicationContext, R.drawable.profile_layer) as LayerDrawable
        drawable?.setColorFilter(foregroundColor, PorterDuff.Mode.SRC_ATOP)
        drawable?.setDrawableByLayerId(
            R.id.foreground_icon,
            ContextCompat.getDrawable(applicationContext, R.drawable.ic_profile_user)
        )
        drawable?.findDrawableByLayerId(R.id.foreground_icon)
            .setColorFilter(backgroundColor, PorterDuff.Mode.SRC_ATOP)

        menu.findItem(R.id.action_settings).setIcon(drawable)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val factory = InjectorUtils.provideGameInformationViewModelFactory()
        val gameInfoViewModel =
            ViewModelProvider(this, factory).get(GameInformationViewModel::class.java)

        val preferences = this.getSharedPreferences(
            Constants.SHARED_PREFERENCES_LOGIN,
            Context.MODE_PRIVATE
        )
        val editor = preferences.edit()
        val intent = Intent(this, AuthActivity::class.java)

        return when (item.itemId) {
            R.id.action_dropdown1 -> {
                val navController = findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.nav_profile)
                return true
            }
            R.id.action_dropdown2 -> {
                profileViewModel.logout()
                editor.remove("isLoggedIn")
                editor.apply()
                startActivity(intent)
                this.finish()
                return true
            }
            android.R.id.home -> {
                // leave the game if user is in a game
//                gameInfoViewModel.leaveGameIfNecessary()
                super.onOptionsItemSelected(item)
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    // Disable back press since drawing undo-redo crashes if back pressed to drawing fragment
//    override fun onBackPressed() {
//        return
//    }
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