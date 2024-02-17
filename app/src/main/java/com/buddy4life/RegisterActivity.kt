package com.buddy4life

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.widget.doOnTextChanged
import com.buddy4life.databinding.ActivityRegisterBinding
import com.buddy4life.model.FirebaseModel
import com.buddy4life.model.User.FirebaseUserModel
import com.buddy4life.model.User.User
import com.buddy4life.model.User.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


private const val REQUIRED = "*required"

class RegisterActivity : AppCompatActivity() {


    private lateinit var binding: ActivityRegisterBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TODO for testing only! delete this line:
        if (UserModel.instance.currentUser() != null) {

            val intent = Intent(this, MainActivity:: class.java)
            startActivity(intent)
//            Firebase.auth.signOut()
        }
//

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

            if (!name.isNullOrEmpty() && !password.isNullOrEmpty() && password.length >= 6 && !email.isNullOrEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString()).matches()) {

                registerUser(name, password, email)
                val intent = Intent(this, LoginActivity:: class.java)
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

                if (binding.etEmail.text.toString().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString()).matches()) {
                    binding.textInputLayoutEmail.error = "Oops! Not a valid email address"
                }

            }

        }

    }


    private fun registerUser(name: String,
                     password: String,
                     email: String) {

        val user = User(name, "photoUrl1", email)
        UserModel.instance.registerUser(user.email, password) {
            if (it?.uid != null) {
                user.uid = it.uid

                UserModel.instance.addUser(user) {
                    Log.d("TAG", "added user")

                }
            } else {
                Log.w("TAG", "Error adding user to Firebase, no UID returned")
            }

        }
    }


}