package com.example.baitap11

import android.os.Bundle
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baitap11.ToDoTask
import com.example.baitap11.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter
    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lấy User ID từ Login gửi sang
        currentUserId = intent.getIntExtra("USER_ID", -1)
        
        setupRecyclerView()
        
        viewModel.setUserId(currentUserId)
        viewModel.tasks.observe(this) { tasks ->
            taskAdapter.updateData(tasks)
        }

        // Sự kiện nút FAB để thêm công việc
        binding.fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(ArrayList(),
            onTaskChecked = { taskId, isChecked ->
                viewModel.updateTaskStatus(taskId, isChecked) // Cập nhật trạng thái
            },
            onTaskDelete = { taskId ->
                deleteTaskConfirm(taskId) // Xóa công việc
            },
            onTaskEdit = { task ->
                showEditTaskDialog(task) // Sửa công việc
            }
        )
        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        binding.rvTasks.adapter = taskAdapter
    }

    private fun showAddTaskDialog() {
        val input = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Thêm công việc mới")
            .setView(input)
            .setPositiveButton("Thêm") { _, _ ->
                val taskName = input.text.toString()
                if (taskName.isNotEmpty()) {
                    viewModel.addTask(taskName)
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showEditTaskDialog(task: ToDoTask) {
        val input = EditText(this)
        input.setText(task.name)
        AlertDialog.Builder(this)
            .setTitle("Sửa công việc")
            .setView(input)
            .setPositiveButton("Lưu") { _, _ ->
                val newTaskName = input.text.toString()
                if (newTaskName.isNotEmpty()) {
                    viewModel.updateTaskName(task.id, newTaskName)
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun deleteTaskConfirm(taskId: Int) {
        AlertDialog.Builder(this)
            .setTitle("Xóa công việc")
            .setMessage("Bạn có chắc chắn muốn xóa?")
            .setPositiveButton("Xóa") { _, _ ->
                viewModel.deleteTask(taskId)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}