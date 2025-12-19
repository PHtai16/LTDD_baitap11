package com.example.baitap11

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baitap11.DatabaseHelper
import com.example.baitap11.ToDoTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = DatabaseHelper(application)
    
    private val _tasks = MutableLiveData<List<ToDoTask>>()
    val tasks: LiveData<List<ToDoTask>> get() = _tasks

    private var currentUserId: Int = -1

    fun setUserId(userId: Int) {
        currentUserId = userId
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            refreshTasks()
        }
    }

    private fun refreshTasks() {
        if (currentUserId != -1) {
            val list = db.getTasks(currentUserId)
            _tasks.postValue(list)
        }
    }

    fun addTask(name: String) {
        if (currentUserId != -1 && name.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                db.addTask(currentUserId, name)
                refreshTasks()
            }
        }
    }

    fun updateTaskStatus(taskId: Int, isCompleted: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            db.updateTaskStatus(taskId, isCompleted)
            refreshTasks()
        }
    }

    fun updateTaskName(taskId: Int, newName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            db.updateTaskName(taskId, newName)
            refreshTasks()
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            db.deleteTask(taskId)
            refreshTasks()
        }
    }
}