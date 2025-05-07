package com.example.hibernatelater

import android.content.Intent
import android.graphics.Typeface
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

            // Group exercises by date
            val groupedByDate = exercises.groupBy { it.date }

            // Iterate over each date group in descending order (most recent date first)
            groupedByDate.toSortedMap(compareByDescending { it }).forEach { (date, exerciseList) ->
                // Add a date header
                val dateTextView = TextView(this)
                dateTextView.text = "$date"
                dateTextView.textSize = 20f
                dateTextView.setTypeface(null, Typeface.BOLD)
                dateTextView.setPadding(0, 16, 0, 8)
                entriesLayout.addView(dateTextView)

                // Add all exercises under that date
                exerciseList.forEach { exercise ->
                    val exerciseTextView = TextView(this)
                    exerciseTextView.text = "${exercise.name} - Sets: ${exercise.sets}, Reps: ${exercise.reps}"
                    exerciseTextView.textSize = 16f
                    entriesLayout.addView(exerciseTextView)
                }
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