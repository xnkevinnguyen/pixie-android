package com.pixie.android.ui.user.register

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
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
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.pixie.android.R
import com.pixie.android.model.user.LoggedInUserView
import com.pixie.android.type.Theme
import com.pixie.android.ui.MainActivity
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils
import kotlinx.android.synthetic.main.register_fragment.*
import kotlin.random.Random


class RegisterFragment : Fragment() {

    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var preferencesSettings: SharedPreferences
    private lateinit var editorSettings: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.register_fragment, container, false)
        val navController = findNavController(requireActivity(), R.id.nav_login_fragment)

        preferencesSettings = requireContext().getSharedPreferences(
            Constants.SHARED_PREFERENCES_SETTING,
            Context.MODE_PRIVATE
        )
        editorSettings = preferencesSettings.edit()

        val toLogin = root.findViewById<ImageView>(R.id.return_login)
        toLogin.setOnClickListener {
            navController.navigate(R.id.nav_login)
        }

        val swipe = root.findViewById<TextView>(R.id.swipeLeft)
        swipe.setOnClickListener {
            navController.navigate(R.id.nav_login)
        }

        val eye1 = root.findViewById<ImageView>(R.id.view_pass)
        val eye2 = root.findViewById<ImageView>(R.id.view2_pass)
        eye1.setOnClickListener {
            toggleHidePassword(eye1, et_password)
        }
        eye2.setOnClickListener {
            toggleHidePassword(eye2, et_repassword)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        val surname = view.findViewById<EditText>(R.id.first_name)
        val name = view.findViewById<EditText>(R.id.last_name)
        val username = view.findViewById<EditText>(R.id.et_name)
        val password = view.findViewById<EditText>(R.id.et_password)
        var retypePassword = view.findViewById<EditText>(R.id.et_repassword)
        val register = view.findViewById<Button>(R.id.btn_register)
        val loading = view.findViewById<ProgressBar>(R.id.loading)
        val errorMessageField = view.findViewById<TextView>(R.id.error_login)

        val theme = view.findViewById<Spinner>(R.id.spinner_theme)
        val french = view.findViewById<RadioButton>(R.id.radio_fr)

        var languageSelected = com.pixie.android.type.Language.ENGLISH
        var languageString = resources.getString(R.string.eng)
        language.setOnCheckedChangeListener { _, _ ->
            if(french.isChecked) {
                languageSelected = com.pixie.android.type.Language.FRENCH
                languageString = resources.getString(R.string.fr)
            }
            else {
                languageSelected = com.pixie.android.type.Language.ENGLISH
                languageString = resources.getString(R.string.eng)
            }
        }

        val itemsTheme = arrayOf(
            resources.getString(R.string.dark),
            resources.getString(R.string.light),
            resources.getString(R.string.christmas),
            resources.getString(R.string.pink),
            resources.getString(R.string.halloween)
        )
        val adapterTheme: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            R.layout.spinner_layout,
            itemsTheme
        )

        theme.adapter = adapterTheme
        theme.setSelection(adapterTheme.getPosition(resources.getString(R.string.dark)))

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
                name.text.toString(),
                surname.text.toString(),
                username.text.toString(),
                password.text.toString(),
                retypePassword.text.toString()
            )
        }


        password.afterTextChanged {
            registerViewModel.registerDataChanged(
                name.text.toString(),
                surname.text.toString(),
                username.text.toString(),
                password.text.toString(),
                retypePassword.text.toString()
            )
        }

        retypePassword.apply {
            afterTextChanged {
                registerViewModel.registerDataChanged(
                    name.text.toString(),
                    surname.text.toString(),
                    username.text.toString(),
                    password.text.toString(),
                    retypePassword.text.toString()
                )
            }


            register.setOnClickListener {
                loading.visibility = View.VISIBLE
                val themeValue = theme.selectedItem.toString()
                val theme = getTheme(themeValue)

                val foregroundColor = Color.argb(255,generateDarkColorInt(), generateDarkColorInt(), generateDarkColorInt())
                val backgroundColor = Color.argb(255, generateLightColorInt(), generateLightColorInt(),generateLightColorInt())
                val foregroundToString = ("#" + Integer.toHexString(foregroundColor).substring(2))
                val backgroundToString = ("#" + Integer.toHexString(backgroundColor).substring(2))

                registerViewModel.register(
                    username.text.toString(),
                    password.text.toString(),
                    name.text.toString(),
                    surname.text.toString(),
                    foregroundToString,
                    backgroundToString,
                    languageSelected,
                    theme
                ) { registerResult ->

                    if (registerResult.success != null) {

                        applyThemeSettings(themeValue)
                        applyLanguageSettings(languageString)
                        // Once register succeeds, we are loggin in the user
                        val intent = Intent(view.context, MainActivity::class.java)
                        val param = Bundle();
                        param.putInt("isNewUser", 1); //Your id
                        intent.putExtras(param)
                        // Store for next time user opens application
                        editor.putBoolean(Constants.SHARED_PREFERENCES_LOGIN_STATUS, true)
                        editor.putString(
                            Constants.USER_ID,
                            registerResult.success.userID.toString()
                        )
                        editor.putString(Constants.USERNAME, registerResult.success.username)
                        editor.apply()
                        startActivity(intent)
                        requireActivity().finish()
                        updateUiWithUser(registerResult.success)


                    } else if (registerResult.error != null) {
                        errorMessageField.text = registerResult.error
                        errorMessageField.visibility = View.VISIBLE
                        showRegisterFail(registerResult.error)
                    }

                    loading.visibility = View.INVISIBLE

                }

            }
        }

        super.onViewCreated(view, savedInstanceState)
    }
    private fun generateLightColorInt():Int{
        return Math.floor((1.0+Random.nextDouble(1.0)) * (Math.pow(16.0,2.0)-1)/2).toInt()
    }
    private fun generateDarkColorInt():Int{
        return Math.floor(Random.nextDouble(1.0) * (Math.pow(16.0,2.0)-1)/2).toInt()
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

    private fun toggleHidePassword(eyeIcon: ImageView, passwordText: EditText) {
        if (passwordText.transformationMethod == PasswordTransformationMethod.getInstance()) {
            eyeIcon.setImageResource(R.drawable.ic_eye)
            passwordText.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            eyeIcon.setImageResource(R.drawable.ic_hide_eye);
            passwordText.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }

    fun getTheme(theme:String):Theme{
        return if (theme == requireContext().resources.getString(R.string.dark)) Theme.DARK
        else if (theme == requireContext().resources.getString(R.string.light)) Theme.LIGHT
        else if (theme == requireContext().resources.getString(R.string.pink)) Theme.BARBIE
        else if (theme == requireContext().resources.getString(R.string.christmas)) Theme.CHRISTMAS
        else Theme.HALLOWEEN

    }

    private fun applyThemeSettings(themeValue: String) {
        // Forcing value in preferences to always be in English and not change because of the language change
        val inputValue =
            if (themeValue == requireContext().resources.getString(R.string.dark)) "Dark"
            else if (themeValue == requireContext().resources.getString(R.string.light)) "Light"
            else if (themeValue == requireContext().resources.getString(R.string.pink)) "Barbie"
            else if (themeValue == requireContext().resources.getString(R.string.christmas)) "Christmas"
            else "Halloween"

        editorSettings.putString(Constants.THEME, inputValue)
        editorSettings.apply()
    }

    private fun applyLanguageSettings(langValue: String) {
        // Forcing value in preferences to always be in English and not change because of the language change
        Log.d("lang", "lang $langValue")
        val inputValue =
            if (langValue == requireContext().resources.getString(R.string.eng)) "English"
            else "French"

        Log.d("lang", "input lang $inputValue")
        editorSettings.putString(Constants.LANGUAGE, inputValue)
        editorSettings.apply()
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