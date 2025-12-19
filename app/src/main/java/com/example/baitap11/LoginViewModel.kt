package com.example.baitap11

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baitap11.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val db = DatabaseHelper(application)

    private val _loginResult = MutableLiveData<LoginState>()
    val loginResult: LiveData<LoginState> get() = _loginResult

    private val _registerResult = MutableLiveData<Boolean>()
    val registerResult: LiveData<Boolean> get() = _registerResult

    val username = MutableLiveData<String>("")
    val password = MutableLiveData<String>("")

    fun login() {
        val user = username.value
        val pass = password.value
        if (!user.isNullOrEmpty() && !pass.isNullOrEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                val userId = db.checkUser(user, pass)
                if (userId != -1) {
                    _loginResult.postValue(LoginState.Success(userId))
                } else {
                    _loginResult.postValue(LoginState.Error("Sai tài khoản hoặc mật khẩu"))
                }
            }
        } else {
            _loginResult.value = LoginState.Error("Vui lòng nhập đủ thông tin")
        }
    }

    fun register() {
        val user = username.value
        val pass = password.value
        if (!user.isNullOrEmpty() && !pass.isNullOrEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                val success = db.registerUser(user, pass)
                _registerResult.postValue(success)
            }
        } else {
            _registerResult.value = false
        }
    }
}

sealed class LoginState {
    data class Success(val userId: Int) : LoginState()
    data class Error(val message: String) : LoginState()
}