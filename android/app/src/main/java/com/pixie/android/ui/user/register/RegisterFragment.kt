package com.pixie.android.ui.user.register

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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.pixie.android.utilities.Constants
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.pixie.android.R
import com.pixie.android.model.user.LoggedInUserView
import com.pixie.android.ui.MainActivity
import com.pixie.android.utilities.InjectorUtils


class RegisterFragment : Fragment() {

    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.register_fragment, container, false)
        val navController = findNavController(requireActivity(), R.id.nav_login_fragment)

        val toLogin = root.findViewById<ImageView>(R.id.return_login)
        toLogin.setOnClickListener {
            navController.navigate(R.id.nav_login)
        }

        val swipe = root.findViewById<TextView>(R.id.swipeLeft)
        swipe.setOnClickListener {
            navController.navigate(R.id.nav_login)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val username = view.findViewById<EditText>(R.id.et_name)
        val password = view.findViewById<EditText>(R.id.et_password)
        var retypePassword = view.findViewById<EditText>(R.id.et_repassword)
        val register = view.findViewById<Button>(R.id.btn_register)
        preferences = requireContext().getSharedPreferences(
            Constants.SHARED_PREFERENCES_LOGIN,
            Context.MODE_PRIVATE
        )
        editor = preferences.edit()


        val factory = InjectorUtils.provideRegisterViewModelFactory()
        registerViewModel = ViewModelProvider(this, factory).get(RegisterViewModel::class.java)

        registerViewModel.getRegisterFormState().observe(viewLifecycleOwner, Observer {
            val loginState = it

            // disable register button unless both username / password and retype password is valid
            register.isEnabled = loginState.isDataValid

            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }

            if (loginState.rePasswordError != null) {
                retypePassword.error = getString(loginState.rePasswordError)
            }
        })


        username.afterTextChanged {
            registerViewModel.registerDataChanged(
                username.text.toString(),
                password.text.toString(),
                retypePassword.text.toString()
            )
        }


        password.afterTextChanged {
            registerViewModel.registerDataChanged(
                username.text.toString(),
                password.text.toString(),
                retypePassword.text.toString()
            )
        }

        retypePassword.apply {
            afterTextChanged {
                registerViewModel.registerDataChanged(
                    username.text.toString(),
                    password.text.toString(),
                    retypePassword.text.toString()
                )
            }


            register.setOnClickListener {
                registerViewModel.register(username.text.toString(), password.text.toString()) {


                    if (it.success != null) {
                        val intent = Intent(view?.context, MainActivity::class.java)
                        // Store for next time user opens application
                        editor.putBoolean(Constants.SHARED_PREFERENCES_LOGIN_STATUS, true)
                        editor.putString(Constants.USER_ID, it.success.userID.toString())
                        editor.putString(Constants.USERNAME, it.success.username)
                        editor.apply()
                        startActivity(intent)
                        requireActivity().finish()
                        updateUiWithUser(it.success)
                    } else if (it.error != null) {
                        showRegisterFail(it.error)
                    }
                }

            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.username
        Toast.makeText(
            requireContext(),
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showRegisterFail(errorString: String) {

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