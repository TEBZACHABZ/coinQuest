package com.example.coinquest.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import android.util.Log
import com.example.coinquest.data.User
import com.example.coinquest.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: AppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AppViewModel::class.java)

        binding.btnRegister.setOnClickListener {
            val username = binding.etRegUsername.text.toString().trim()
            val password = binding.etRegPassword.text.toString().trim()
            val confirmPassword = binding.etRegConfirmPassword.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    lifecycleScope.launch {
                        try {
                            Log.d("RegisterActivity", "Checking if user exists: $username")
                            val existingUser = viewModel.loginUser(username)
                            if (existingUser == null) {
                                Log.d("RegisterActivity", "User doesn't exist. Registering...")
                                viewModel.registerUser(User(username = username, password = password))
                                Log.d("RegisterActivity", "Registration successful for: $username")
                                Toast.makeText(this@RegisterActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Log.d("RegisterActivity", "Username already exists: $username")
                                Toast.makeText(this@RegisterActivity, "Username already exists", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Log.e("RegisterActivity", "Error during registration", e)
                            Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }
}