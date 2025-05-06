package com.example.hibernatelater

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class JournalActivity : AppCompatActivity() {
    private lateinit var homeButton : Button
    private lateinit var clearButton : Button
    private lateinit var entriesLayout : LinearLayout
    private lateinit var exerciseDao: ExerciseDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.journal)

        homeButton = findViewById( R.id.home_button )
        clearButton = findViewById(R.id.clear_journal_button)
        entriesLayout = findViewById(R.id.entriesLayout)

        val db = AppDatabase.getDatabase(this)
        exerciseDao = db.exerciseDao()

        // Fetch exercises from database and display them
        exerciseDao.getAllExercises().observe(this, Observer { exercises ->
            // Clear existing views
            entriesLayout.removeAllViews()

            // Add each exercise as a new TextView
            exercises.forEach { exercise ->
                val textView = TextView(this)
                textView.text = "${exercise.date} - ${exercise.name}\nSets: ${exercise.sets}, Reps: ${exercise.reps}"
                textView.textSize = 18f

                // Add the new TextView to the top of the layout
                entriesLayout.addView(textView, 0)
            }
        })

        homeButton.setOnClickListener { goBack() }
        clearButton.setOnClickListener { clearJournal() }
    }

    // go back to the home page
    fun goBack() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Clear the journal and the database
    fun clearJournal() {
        lifecycleScope.launch {
            exerciseDao.clearAllExercises()
        }
        // Clear UI
        entriesLayout.removeAllViews()
    }

}