package com.pixie.android.ui


import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ImageView
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
import com.pixie.android.ui.chat.ChatViewModel
import com.pixie.android.utilities.InjectorUtils
import com.pixie.android.utilities.OnApplicationStopService
import kotlinx.android.synthetic.main.main_activity.*


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val factory = InjectorUtils.provideChatViewModelFactory()
        val chatViewModel = ViewModelProvider(this, factory).get(ChatViewModel::class.java)
        startService(Intent(baseContext, OnApplicationStopService::class.java))


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_home, R.id.nav_drawing), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val header: View = navView.getHeaderView(0)
        var avatar: ImageView = header.findViewById(R.id.imageView)
        avatar.setOnClickListener {
            navController.navigate(R.id.nav_profile)
            drawerLayout.closeDrawer(GravityCompat.START, false)
        }

        settings.setOnClickListener {
            navController.navigate(R.id.nav_settings)
            drawerLayout.closeDrawer(GravityCompat.START, false)
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
        chatViewModel.startChannel()
        super.onPostCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Added 3 dots in the right up corner
        menuInflater.inflate(R.menu.main, menu)
        return true
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
        chatViewModel.stopChannel()
        super.onDestroy()
    }

}