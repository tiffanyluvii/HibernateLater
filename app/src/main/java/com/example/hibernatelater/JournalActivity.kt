package com.example.hibernatelater

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JournalActivity : AppCompatActivity() {
    private lateinit var homeButton : Button
    private lateinit var clearButton : Button
    private lateinit var entriesLayout : LinearLayout
    private lateinit var exerciseDao: ExerciseDao
    private lateinit var shareButton : Button
    val todayExercises = mutableListOf<ExerciseEntity>() // list of exercises for the day

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.journal)

        homeButton = findViewById( R.id.home_button )
        clearButton = findViewById(R.id.clear_journal_button)
        entriesLayout = findViewById(R.id.entriesLayout)
        shareButton = findViewById( R.id.share_button )

        val db = AppDatabase.getDatabase(this)
        exerciseDao = db.exerciseDao()

        // Fetch exercises from database and display them
        exerciseDao.getAllExercises().observe(this, Observer { exercises ->
            // Clear existing views
            entriesLayout.removeAllViews()

            todayExercises.clear()

            val currentDate = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())

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

                    // add to today's list of exercises if the date matches today's date
                    if (date == currentDate) {
                        todayExercises.add(exercise)
                    }
                }
            }
        })

        homeButton.setOnClickListener { goBack() }
        clearButton.setOnClickListener { clearJournal() }

        shareButton.setOnClickListener {
            val currentDate = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())
            val summary = generateWorkoutSummary(currentDate, todayExercises)
            sendEmail(summary)
        }
    }

    // generate the text for the email body
    fun generateWorkoutSummary(date: String, exercises: List<ExerciseEntity>): String {
        if (todayExercises.isEmpty()) return "No workouts logged for $date."

        val result = StringBuilder()
        result.append("Workout Summary for $date\n\n")
        exercises.forEachIndexed { index, entry ->
            result.append("${index + 1}. ${entry.name}: ${entry.sets} sets , ${entry.reps} reps\n")
        }
        return result.toString()
    }

    // send the email
    fun sendEmail(body: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_SUBJECT, "Your Workout Summary! - HibernateLater")
            putExtra(Intent.EXTRA_TEXT, body)
        }
        startActivity(Intent.createChooser(intent, "Send workout summary via email"))

    }

    // go back to the home page
    fun goBack() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // clear the journal and the database
    fun clearJournal() {
        lifecycleScope.launch {
            exerciseDao.clearAllExercises()
        }
        // clear UI
        entriesLayout.removeAllViews()
    }


}