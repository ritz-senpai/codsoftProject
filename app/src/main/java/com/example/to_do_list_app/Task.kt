package com.example.to_do_list_app

data class Task(var title: String,
                var description: String,
                var isCompleted: Boolean = false
)
