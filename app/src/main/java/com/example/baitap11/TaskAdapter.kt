package com.example.baitap11

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.baitap11.ToDoTask
import com.example.baitap11.databinding.ItemTaskBinding

class TaskAdapter(
    private var taskList: List<ToDoTask>,
    private val onTaskChecked: (Int, Boolean) -> Unit, // Callback khi check box
    private val onTaskDelete: (Int) -> Unit,           // Callback khi bấm xóa
    private val onTaskEdit: (ToDoTask) -> Unit             // Callback khi bấm vào item để sửa
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        
        holder.binding.apply {
            tvTaskName.text = task.name
            
            // Xóa listener cũ trước khi gán trạng thái để tránh trigger sai
            cbCompleted.setOnCheckedChangeListener(null)
            
            cbCompleted.isChecked = task.isCompleted

            // Gạch ngang chữ nếu đã hoàn thành
            toggleStrikeThrough(tvTaskName, task.isCompleted)

            cbCompleted.setOnCheckedChangeListener { _, isChecked ->
                toggleStrikeThrough(tvTaskName, isChecked)
                onTaskChecked(task.id, isChecked)
            }

            btnDelete.setOnClickListener {
                onTaskDelete(task.id)
            }

            // Sự kiện click vào item để sửa
            root.setOnClickListener {
                onTaskEdit(task)
            }
        }
    }

    private fun toggleStrikeThrough(tv: TextView, isCompleted: Boolean) {
        if (isCompleted) {
            tv.paintFlags = tv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            tv.paintFlags = tv.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    override fun getItemCount() = taskList.size

    fun updateData(newList: List<ToDoTask>) {
        taskList = newList
        notifyDataSetChanged()
    }
}