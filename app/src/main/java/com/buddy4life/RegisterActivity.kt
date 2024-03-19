package com.buddy4life

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.buddy4life.databinding.ActivityRegisterBinding
import com.buddy4life.model.User.User
import com.buddy4life.model.User.UserModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private const val REQUIRED = "required*"

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Firebase.auth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.tvToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.etFullName.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty()) {
                binding.textInputLayoutFullName.error = null
            } else {
                binding.textInputLayoutFullName.error = REQUIRED
            }
        }

        binding.etPassword.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty()) {
                binding.textInputLayoutPassword.error = null
            } else {
                binding.textInputLayoutPassword.error = REQUIRED
            }
        }

        binding.etEmail.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty()) {
                binding.textInputLayoutEmail.error = null
            } else {
                binding.textInputLayoutEmail.error = REQUIRED
            }
        }

        binding.btnRegister.setOnClickListener {
            val name: String? = binding.etFullName.text?.toString()
            val password: String? = binding.etPassword.text?.toString()
            val email: String? = binding.etEmail.text?.toString()

            if (!name.isNullOrEmpty() && !password.isNullOrEmpty() && password.length >= 6 && !email.isNullOrEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(
                    binding.etEmail.text.toString()
                ).matches()
            ) {
                registerUser(name, password, email)
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                if (binding.etFullName.text.toString().isEmpty()) {
                    binding.textInputLayoutFullName.error = REQUIRED
                }
                if (binding.etPassword.text.toString().isEmpty()) {
                    binding.textInputLayoutPassword.error = REQUIRED
                }
                if (binding.etPassword.text.toString().length < 6) {
                    binding.textInputLayoutPassword.error = "Password must be at least 6 characters"
                }

                if (binding.etEmail.text.toString()
                        .isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString())
                        .matches()
                ) {
                    binding.textInputLayoutEmail.error = "Oops! Not a valid email address"
                }
            }
        }
    }

    private fun registerUser(name: String, password: String, email: String) {
        var user = User(name, null, email)
        UserModel.instance.registerUser(user, password) { registeredUser ->
            if (registeredUser?.uid != null) {
                Log.d("TAG", "user photo url retured is: ${registeredUser.photoUrl}")

                UserModel.instance.addUser(registeredUser) {
                    Log.d("TAG", "added user")
                }
            } else {
                Log.w("TAG", "Error adding user to Firebase, no UID returned")
            }
        }
    }
}