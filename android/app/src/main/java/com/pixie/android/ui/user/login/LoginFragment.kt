package com.pixie.android.ui.user.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.pixie.android.R
import com.pixie.android.model.user.LoggedInUserView
import com.pixie.android.ui.draw.MainActivity
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils

class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val root =  inflater.inflate(R.layout.login_fragment, container, false)
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_login_fragment)
        val toRegister : TextView = root.findViewById(R.id.button_register)
        toRegister.setOnClickListener {
            navController.navigate(R.id.nav_register)
        }
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val username = view.findViewById<EditText>(R.id.username)
        val password = view.findViewById<EditText>(R.id.password)
        val login = view.findViewById<Button>(R.id.login)
        val loading = view.findViewById<ProgressBar>(R.id.loading)
        val errorMessageField = view.findViewById<TextView>(R.id.error_login)
        preferences = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCES_LOGIN, Context.MODE_PRIVATE)
        editor = preferences.edit()

        val intent = Intent(view.context, MainActivity::class.java)

        val factory = InjectorUtils.provideLoginViewModelFactory()
        loginViewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)

        if (preferences.getBoolean(Constants.SHARED_PREFERENCES_LOGIN_STATUS, false)) {
            startActivity(intent)
            requireActivity().finish()
        }
        //to remove
        startActivity(intent)


        loginViewModel.getLoginFormState().observe(viewLifecycleOwner, Observer {
            val loginState = it

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
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
                loginViewModel.login(username.text.toString(), password.text.toString()) {
                    loading.visibility = View.INVISIBLE
                    if (it.success != null){
                        val intent = Intent(view?.context, MainActivity::class.java)

                        editor.putBoolean(Constants.SHARED_PREFERENCES_LOGIN, true)
                        editor.apply()
                        startActivity(intent)
                        requireActivity().finish()
                        updateUiWithUser(it.success)
                    }else if (it?.error!=null){
                        errorMessageField.text = it.error
                        errorMessageField.visibility = View.VISIBLE
                        showLoginFailed(it.error)
                    }

                }

            }
        }

        super.onViewCreated(view, savedInstanceState)
    }



    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        Toast.makeText(
            requireContext(),
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed( errorString: String) {
        Toast.makeText(requireContext(), errorString, Toast.LENGTH_SHORT).show()
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