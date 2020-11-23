package com.pixie.android.ui.user.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.pixie.android.R
import com.pixie.android.model.user.LoggedInUserView
import com.pixie.android.type.Language
import com.pixie.android.ui.MainActivity
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.Constants.Companion.LANGUAGE_ENGLISH
import com.pixie.android.utilities.Constants.Companion.LANGUAGE_FRENCH
import com.pixie.android.utilities.InjectorUtils
import kotlinx.android.synthetic.main.login_fragment.*

class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.login_fragment, container, false)
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_login_fragment)
        val toRegister: TextView = root.findViewById(R.id.button_register)
        toRegister.setOnClickListener {
            navController.navigate(R.id.nav_register)
        }

        var color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        color = ContextCompat.getColor(requireContext(), R.color.colorFourth)
        val eye = root.findViewById<ImageView>(R.id.eye)
        eye.setOnClickListener {
            if(password.transformationMethod == PasswordTransformationMethod.getInstance()){
                eye.setImageResource(R.drawable.ic_eye)
                password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
            else {
                eye.setImageResource(R.drawable.ic_hide_eye);
                password.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val username = view.findViewById<EditText>(R.id.username)
        val password = view.findViewById<EditText>(R.id.password)
        val login = view.findViewById<Button>(R.id.login)
        val loading = view.findViewById<ProgressBar>(R.id.loading)
        val errorMessageField = view.findViewById<TextView>(R.id.error_login)
        preferences = requireContext().getSharedPreferences(
            Constants.SHARED_PREFERENCES_SETTING,
            Context.MODE_PRIVATE
        )
        editor = preferences.edit()

        val factory = InjectorUtils.provideLoginViewModelFactory()
        loginViewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)


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
                    if (it.success != null && activity != null) {
                        if(it.success.language!=null){
                            if(it.success.language.equals(Language.ENGLISH)){
                                editor.putString(Constants.LANGUAGE, LANGUAGE_ENGLISH)

                            }else{
                                editor.putString(Constants.LANGUAGE, LANGUAGE_FRENCH)

                            }
                        }
                        if(it.success.theme !=null){
                            editor.putString(Constants.THEME, it.success.theme)

                        }
                        editor.apply()
                        Log.d("LoginFragment","Set User Language")
                        val intent = Intent(view?.context, MainActivity::class.java)

                        // Store for next time user opens application
                        editor.putBoolean(Constants.SHARED_PREFERENCES_LOGIN_STATUS, true)
                        editor.putString(Constants.USER_ID, it.success.userID.toString())
                        editor.putString(Constants.USERNAME, it.success.username)
                        editor.apply()
                        startActivity(intent)
                        requireActivity().finish()
                        updateUiWithUser(it.success)
                    } else if (it?.error != null) {
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
        val displayName = model.username
        Toast.makeText(
            requireContext(),
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(errorString: String) {
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