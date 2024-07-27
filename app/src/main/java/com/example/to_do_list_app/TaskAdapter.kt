package com.example.to_do_list_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onTaskClickListener: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task)
        holder.itemView.setOnClickListener {
            onTaskClickListener(task)
        }
    }

    override fun getItemCount(): Int = tasks.size

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskCheckBox: CheckBox = itemView.findViewById(R.id.taskCheckBox)
        private val taskTitleTextView: TextView = itemView.findViewById(R.id.taskTitleTextView)
        private val descriptionTextView:TextView=itemView.findViewById(R.id.descriptionTextView)

        fun bind(task: Task) {
            taskCheckBox.isChecked = task.isCompleted
            taskTitleTextView.text = task.title
            descriptionTextView.text=task.description
            taskCheckBox.setOnCheckedChangeListener { _, isChecked ->
                task.isCompleted = isChecked
                saveTasks(itemView.context)
            }
            itemView.setOnClickListener {
                onTaskClickListener(task)
            }
        }
    }
    private fun saveTasks(context: Context) {
        val sharedPreferences = context.getSharedPreferences("tasks", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(tasks)
        editor.putString("task_list", json)
        editor.apply()
    }
}