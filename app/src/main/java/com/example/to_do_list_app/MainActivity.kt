package com.example.to_do_list_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import androidx.appcompat.app.AlertDialog;
import com.google.gson.reflect.TypeToken


class MainActivity : AppCompatActivity() {

    private lateinit var taskAdapter: TaskAdapter
    private val tasks = mutableListOf<Task>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Check if the user has set a username
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)
        if(username==null){
            // If username is not set, start WelcomeActivity
            val intent = Intent(this, Welcome::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        val usernameTextView: TextView = findViewById(R.id.usernameTextView)
        val settingButton: ImageButton = findViewById(R.id.settingButton)


        taskAdapter = TaskAdapter(tasks) { task ->
            showAddEditTaskDialog(task)
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter

        findViewById<Button>(R.id.addTaskButton).setOnClickListener {
            showAddEditTaskDialog(null)
        }

        loadTasks()
        loadUsername(usernameTextView)
        settingButton.setOnClickListener {
            showChangeUsernameDialog()
        }


    }
    private fun loadUsername(usernameTextView: TextView) {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "Guest")
        usernameTextView.text = "Welcome, \n$username!"
    }

    private fun showChangeUsernameDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.activity_dialog_change_username, null)
        val dialogBuilder = AlertDialog.Builder(this).setView(dialogView)

        val usernameEditText: EditText = dialogView.findViewById(R.id.usernameEditText)

        dialogBuilder.setTitle("Change Username")

        dialogBuilder.setPositiveButton("Save") { dialog, _ ->
            val newUsername = usernameEditText.text.toString()
            if (newUsername.isNotEmpty()) {
                val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    putString("username", newUsername)
                    apply()
                }
                findViewById<TextView>(R.id.usernameTextView).text = "Welcome, \n$newUsername!"
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        dialogBuilder.create().show()
    }



    private fun showAddEditTaskDialog(task: Task?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_task, null)
        val dialogBuilder = AlertDialog.Builder(this).setView(dialogView)

        val titleEditText: EditText = dialogView.findViewById(R.id.taskTitleEditText)
        val descriptionEditText: EditText = dialogView.findViewById(R.id.taskDescriptionEditText)

        if (task != null) {
            titleEditText.setText(task.title)
            descriptionEditText.setText(task.description)
            dialogBuilder.setTitle("Edit Task")
        } else {
            dialogBuilder.setTitle("Add Task")
        }

        dialogBuilder.setPositiveButton("Save") { dialog, _ ->
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()

            if (task != null) {
                task.title = title
                task.description = description
            } else {
                tasks.add(Task(title, description))
            }

            taskAdapter.notifyDataSetChanged()
            saveTasks()
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        if (task != null) {
            dialogBuilder.setNeutralButton("Delete") { dialog, _ ->
                tasks.remove(task)
                taskAdapter.notifyDataSetChanged()
                saveTasks()
                dialog.dismiss()
            }
        }

        dialogBuilder.create().show()
    }

    private fun loadTasks() {
        val sharedPreferences = getSharedPreferences("tasks", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("task_list", null)
        if (json != null) {
            val type = object : TypeToken<List<Task>>() {}.type
            tasks.addAll(Gson().fromJson(json, type))
            taskAdapter.notifyDataSetChanged()
        }
    }

    private fun saveTasks() {
        val sharedPreferences = getSharedPreferences("tasks", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(tasks)
        editor.putString("task_list", json)
        editor.apply()
    }
}
