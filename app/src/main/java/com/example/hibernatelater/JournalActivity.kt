package com.example.hibernatelater

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class JournalActivity : AppCompatActivity() {
    private lateinit var homeButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.journal)

        homeButton = findViewById( R.id.home_button )

        homeButton.setOnClickListener { goBack() }
    }

    // go back to the home page
    fun goBack() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}