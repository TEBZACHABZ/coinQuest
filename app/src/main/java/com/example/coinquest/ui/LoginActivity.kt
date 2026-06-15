package com.example.coinquest.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import android.util.Log
import com.example.coinquest.MainActivity
import com.example.coinquest.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AppViewModel::class.java]

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        Log.d("LoginActivity", "Attempting login for user: $username")
                        val user = viewModel.loginUser(username)
                        if (user != null) {
                            Log.d("LoginActivity", "User found in database. Checking password...")
                            if (user.password == password) {
                                Log.d("LoginActivity", "Login successful!")
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            } else {
                                Log.d("LoginActivity", "Password mismatch.")
                                Toast.makeText(this@LoginActivity, "Invalid password", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Log.d("LoginActivity", "No user found with username: $username")
                            Toast.makeText(this@LoginActivity, "Username not found. Please register.", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("LoginActivity", "Error during login", e)
                        Toast.makeText(this@LoginActivity, "An error occurred: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}