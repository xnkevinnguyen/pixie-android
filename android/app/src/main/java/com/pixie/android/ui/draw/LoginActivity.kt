package com.pixie.android.ui.draw

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.model.login.LoggedInUserView
import com.pixie.android.ui.draw.login.LoginViewModel
import com.pixie.android.utilities.InjectorUtils

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    lateinit var preferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.login_activity)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)
        val error_message = findViewById<TextView>(R.id.error_login)
        val intent: Intent = Intent(this, DrawActivity::class.java)
        preferences = getSharedPreferences("Login", Context.MODE_PRIVATE)
        editor = preferences.edit()

        val factory = InjectorUtils.provideLoginViewModelFactory()
        loginViewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)

        if (preferences.getBoolean("isLoggedIn", false)) {
            startActivity(intent)
            finish()
        }

        loginViewModel.getLoginFormState().observe(this, Observer {
            val loginState = it

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.getLoginResultState().observe(this, Observer {
            val loginResult = it

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }



            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                val wasLoginSuccessful = loginViewModel.login(username.text.toString(), password.text.toString())
                if (wasLoginSuccessful){
                    editor.putBoolean("isLoggedIn", true)
                    editor.apply()
                    startActivity(intent)
                    finish()
                } else {
                    error_message.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}