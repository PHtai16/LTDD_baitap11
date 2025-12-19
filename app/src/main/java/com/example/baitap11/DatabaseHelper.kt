package com.example.baitap11

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ToDoListApp.db"
        // Tăng version lên 2 để cập nhật lại cấu trúc bảng
        private const val DATABASE_VERSION = 2

        // Bảng Users
        const val TABLE_USERS = "users"
        const val COL_USER_ID = "id"
        const val COL_USERNAME = "username"
        const val COL_PASSWORD = "password"

        // Bảng Tasks
        const val TABLE_TASKS = "tasks"
        const val COL_TASK_ID = "id"
        const val COL_TASK_USER_ID = "user_id" // Khóa ngoại để biết việc của ai
        const val COL_TASK_NAME = "name"
        const val COL_IS_COMPLETED = "is_completed"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Tạo bảng User
        val createUsersTable = "CREATE TABLE $TABLE_USERS ($COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_USERNAME TEXT, $COL_PASSWORD TEXT)"
        db.execSQL(createUsersTable)

        // Tạo bảng Tasks
        val createTasksTable = "CREATE TABLE $TABLE_TASKS ($COL_TASK_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_TASK_USER_ID INTEGER, $COL_TASK_NAME TEXT, $COL_IS_COMPLETED INTEGER)"
        db.execSQL(createTasksTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Xóa bảng cũ nếu tồn tại để tạo lại
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    // --- CHỨC NĂNG USER (Đăng ký / Đăng nhập) ---

    fun registerUser(user: String, pass: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_USERNAME, user)
            put(COL_PASSWORD, pass)
        }
        val result = db.insert(TABLE_USERS, null, values)
        return result != -1L
    }

    fun checkUser(user: String, pass: String): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COL_USER_ID FROM $TABLE_USERS WHERE $COL_USERNAME=? AND $COL_PASSWORD=?", arrayOf(user, pass))
        var userId = -1
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0)
        }
        cursor.close()
        return userId // Trả về -1 nếu sai, trả về ID nếu đúng
    }

    // --- CHỨC NĂNG TASK (Thêm / Xóa / Sửa / Xem) ---

    fun addTask(userId: Int, taskName: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_TASK_USER_ID, userId)
            put(COL_TASK_NAME, taskName)
            put(COL_IS_COMPLETED, 0)
        }
        return db.insert(TABLE_TASKS, null, values)
    }

    fun getTasks(userId: Int): ArrayList<ToDoTask> {
        val taskList = ArrayList<ToDoTask>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TASKS WHERE $COL_TASK_USER_ID=?", arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TASK_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_NAME))
                val completed = cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_COMPLETED)) == 1
                taskList.add(ToDoTask(id, name, completed))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return taskList
    }

    fun updateTaskStatus(taskId: Int, isCompleted: Boolean) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_IS_COMPLETED, if (isCompleted) 1 else 0)
        }
        db.update(TABLE_TASKS, values, "$COL_TASK_ID=?", arrayOf(taskId.toString()))
    }

    fun updateTaskName(taskId: Int, newName: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_TASK_NAME, newName)
        }
        db.update(TABLE_TASKS, values, "$COL_TASK_ID=?", arrayOf(taskId.toString()))
    }

    fun deleteTask(taskId: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_TASKS, "$COL_TASK_ID=?", arrayOf(taskId.toString()))
    }
}