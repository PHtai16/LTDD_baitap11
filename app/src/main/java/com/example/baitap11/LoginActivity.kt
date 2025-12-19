package com.example.baitap11

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.baitap11.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Binding events manually (ViewBinding style)
        binding.btnLogin.setOnClickListener {
            viewModel.username.value = binding.etUsername.text.toString()
            viewModel.password.value = binding.etPassword.text.toString()
            viewModel.login()
        }

        binding.btnRegister.setOnClickListener {
            viewModel.username.value = binding.etUsername.text.toString()
            viewModel.password.value = binding.etPassword.text.toString()
            viewModel.register()
        }

        // Observe Login Result
        viewModel.loginResult.observe(this) { state ->
            when (state) {
                is LoginState.Success -> {
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("USER_ID", state.userId)
                    startActivity(intent)
                    finish()
                }
                is LoginState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Observe Register Result
        viewModel.registerResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Đăng ký thành công! Hãy đăng nhập.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Tài khoản đã tồn tại hoặc lỗi", Toast.LENGTH_SHORT).show()
            }
        }
    }
}