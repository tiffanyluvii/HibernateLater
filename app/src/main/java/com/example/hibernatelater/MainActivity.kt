package com.example.hibernatelater

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Timer

class MainActivity : AppCompatActivity() {
    private lateinit var bearIcon: View
    private lateinit var yesButton: AppCompatButton
    private lateinit var noButton: AppCompatButton
    private lateinit var questionPrompt: TextView
    private lateinit var xButton: AppCompatButton
    private lateinit var awardButton: AppCompatButton
    private lateinit var journalButton: AppCompatButton
    private lateinit var calendarButton: AppCompatButton
    private lateinit var exerciseNumber: TextView
    private lateinit var enterButton: AppCompatButton

    private lateinit var exerciseInput: EditText
    private lateinit var setInput: EditText
    private lateinit var repInput: EditText

    private lateinit var currentExercise: Exercise

    private lateinit var startScreenBottom: LinearLayout
    private lateinit var exerciseScreenBottom: LinearLayout

    private var checkExercise: Boolean = false

    private lateinit var homepage: HomePage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        buildViewByCode()
    }

    fun animateBear(){
        if (homepage.currentlyExercising()){
            if (!checkExercise){
                bearIcon.setBackgroundResource(R.drawable.dumbbell_bear)
            } else {
                bearIcon.setBackgroundResource(R.drawable.dumbbell_bear_up)
            }
            checkExercise = !checkExercise
        } else {
            // change this to a sleeping bear later
            bearIcon.setBackgroundResource(R.drawable.regular_bear)
        }
    }

    fun buildViewByCode(){
        setContentView(R.layout.activity_main)

        bearIcon = findViewById(R.id.bear)
        yesButton = findViewById(R.id.yes_button)
        noButton = findViewById(R.id.no_button)
        questionPrompt = findViewById(R.id.question)

        xButton = findViewById(R.id.x_icon)
        awardButton = findViewById(R.id.award_icon)
        journalButton = findViewById(R.id.journal_icon)
        calendarButton = findViewById(R.id.calendar_icon)

        startScreenBottom = findViewById(R.id.bottomHalfStartScreen)
        exerciseScreenBottom = findViewById(R.id.bottomHalfWorkoutScreen)

        exerciseNumber = findViewById(R.id.exerciseQuestion)
        enterButton = findViewById(R.id.enterButton)
        exerciseInput = findViewById(R.id.exerciseInput)
        setInput = findViewById(R.id.setsInput)
        repInput = findViewById(R.id.repsInput)


        homepage = HomePage(this)

        yesButton.setOnClickListener{clickYes()}
        noButton.setOnClickListener{clickNo()}
        enterButton.setOnClickListener{enterExercise()}
        xButton.setOnClickListener{pressX()}

        var timer: Timer = Timer()
        var task: ExerciseTimerTask = ExerciseTimerTask(this)
        timer.schedule(task, 0, 700)
    }

    fun clickYes(){
        // the yes button will be reused so check the current prompt to determine what to do
        var currentPrompt = this.questionPrompt.text.toString()

        if (currentPrompt == getString(R.string.start_message)){
            startWorkout()
        } else if (currentPrompt == getString(R.string.end_message)){
            endExercise()
        } else if (currentPrompt == getString(R.string.before_break_message)){
            startBreak()
        } else if (currentPrompt == getString(R.string.after_break_message)){
            startWorkout()
        }

    }

    fun startWorkout(){
        homepage.endBreak()
        homepage.startExercise()
        startScreenBottom.visibility = View.GONE
        exerciseScreenBottom.visibility = View.VISIBLE
        exerciseNumber.text = (getString(R.string.exercise_message) + homepage.getCurrentExerciseNumber())
        homepage.incrementExerciseNumber()
    }

    fun startBreak(){
        homepage.endExercise()
        homepage.startBreak()
    }

    fun clickNo(){
        // the no button will be reused so check the current prompt to determine what to do
        var currentPrompt = this.questionPrompt.text.toString()

        if (currentPrompt == getString(R.string.after_break_message)){
            // rerun the timer

        } else if (currentPrompt == getString(R.string.end_message)){
            // go back to the most recent view
        }
    }

    fun enterExercise(){
        var exerciseType: String = exerciseInput.text.toString()
        var sets: Int = setInput.text.toString().toInt()
        var reps: Int = repInput.text.toString().toInt()

        currentExercise = Exercise(exerciseType, sets, reps)

        homepage.addToList(currentExercise) // probably add it to the journal as well

        startScreenBottom.visibility = View.VISIBLE
        exerciseScreenBottom.visibility = View.GONE
        questionPrompt.text = getString(R.string.before_break_message)

    }

    fun pressX(){
        homepage.endExercise()
        homepage.endBreak()
        questionPrompt.text = getString(R.string.end_message)
    }

    fun endExercise(){
        questionPrompt.text = getString(R.string.start_message)
        // reset the current exercise number
    }

}


