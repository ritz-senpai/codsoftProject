package com.example.to_do_list_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class Welcome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val usernameInputLayout: TextInputLayout = findViewById(R.id.usernameInputLayout)
        val usernameEditText: TextInputEditText = findViewById(R.id.usernameEditText)
        val continueButton: Button = findViewById(R.id.continueButton)

        continueButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            if(username.isNotEmpty()){
                val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    putString("username", username)
                    apply()
                }
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show()
            }
        }

    }
}