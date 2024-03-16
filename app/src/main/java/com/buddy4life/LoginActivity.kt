package com.buddy4life

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.buddy4life.databinding.ActivityLoginBinding
import com.buddy4life.model.User.UserModel
import com.google.firebase.auth.FirebaseUser


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email: String? = binding.etEmail.text?.toString()
            val password: String? = binding.etPassword.text?.toString()

            if (!email.isNullOrEmpty() && !password.isNullOrEmpty() && password.length >= 6 && !email.isNullOrEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(
                    binding.etEmail.text.toString()
                ).matches()
            ) {
                signInUser(email, password) { user ->
                    if (user != null) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            } else {
                if (binding.etEmail.text.toString()
                        .isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString())
                        .matches()
                ) {
                    Toast.makeText(
                        baseContext,
                        "Email is not valid, try again",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                if (binding.etPassword.text.toString().length < 6) {
                    Toast.makeText(
                        baseContext,
                        "Password must be at least 6 characters",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }
    }

    private fun signInUser(
        email: String, password: String, callback: (FirebaseUser?) -> Unit
    ) {
        UserModel.instance.signInUser(email, password) { user ->
            if (user != null) {
                Toast.makeText(
                    baseContext,
                    "Login succeeded!",
                    Toast.LENGTH_SHORT,
                ).show()

                callback(user)
            } else {
                Toast.makeText(
                    baseContext,
                    "Email or Password are incorrect! try again",
                    Toast.LENGTH_SHORT,
                ).show()

                callback(null)
            }
        }
    }
}