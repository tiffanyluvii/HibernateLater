package com.example.hibernatelater

import android.os.Bundle
import android.util.Log
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
    private lateinit var motivationSpacer: View
    private lateinit var motivationMessage: TextView

    private lateinit var exerciseInput: EditText
    private lateinit var setInput: EditText
    private lateinit var repInput: EditText

    private lateinit var currentExercise: Exercise

    private lateinit var startScreenBottom: LinearLayout
    private lateinit var exerciseScreenBottom: LinearLayout

    private var checkExercise: Boolean = false
    private lateinit var currentMessage: String

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
        } else {
            // change this to a sleeping bear later
            bearIcon.setBackgroundResource(R.drawable.regular_bear)
        }
        checkExercise = !checkExercise
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

        motivationMessage = findViewById(R.id.motivation)
//        motivationSpacer = findViewById(R.id.motivationSpacer)

        currentMessage = getString(R.string.start_message)

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
            if (currentExercise.finishedExercise()){
//                currentExercise.resetSets() you can't reset it but idk if it's necessarily
                startWorkout()
            } else {
                currentExercise.incrementSet()
                beforeBreakScreen()
            }
        }

    }

    fun startWorkout(){
        homepage.endBreak()
        homepage.startExercise()

//        motivationSpacer.visibility = View.VISIBLE
        motivationMessage.visibility = View.GONE

        startScreenBottom.visibility = View.GONE
        exerciseScreenBottom.visibility = View.VISIBLE
        exerciseNumber.text = (getString(R.string.exercise_message) + homepage.getCurrentExerciseNumber())
        currentMessage = exerciseNumber.text.toString()
        homepage.incrementExerciseNumber()
    }

    fun startBreak(){
        homepage.endExercise()
        homepage.startBreak()

//        motivationSpacer.visibility = View.GONE
        motivationMessage.visibility = View.GONE

        // for testing purposes DELETE THIS
        questionPrompt.text = getString(R.string.after_break_message)
        currentMessage = getString(R.string.after_break_message)

        // display the timer view
    }

    fun clickNo(){
        // the no button will be reused so check the current prompt to determine what to do
        var currentPrompt = this.questionPrompt.text.toString()

        if (currentPrompt == getString(R.string.after_break_message)){
            // rerun the timer

        } else if (currentPrompt == getString(R.string.end_message)){
            // go back to the most recent view
            if (currentMessage == exerciseNumber.text.toString()){
                // check if they were in the exercise screen
                homepage.endBreak()
                homepage.startExercise()
                startScreenBottom.visibility = View.GONE
                exerciseScreenBottom.visibility = View.VISIBLE
            } else if (currentMessage == getString(R.string.start_message)){
                questionPrompt.text = getString(R.string.start_message)
                currentMessage = getString(R.string.start_message)
            } else if (currentMessage == getString(R.string.before_break_message)){
                homepage.endBreak()
                homepage.startExercise()
                questionPrompt.text = getString(R.string.before_break_message)
                currentMessage = getString(R.string.before_break_message)
            } else if (currentMessage == getString(R.string.after_break_message)){
                questionPrompt.text = getString(R.string.after_break_message)
                currentMessage = getString(R.string.after_break_message)
            }
        }
    }

    fun enterExercise(){

        var exerciseType: String = exerciseInput.text.toString()

        if (exerciseType == ""){
            exerciseInput.hint = "Please enter an exercise!"
        } else if (setInput.text.toString() == ""){
            exerciseNumber.text = "Please enter sets!"
        } else if (repInput.text.toString() == ""){
            exerciseNumber.text = "Please enter reps!"
        } else {
            var sets: Int = setInput.text.toString().toInt()
            var reps: Int = repInput.text.toString().toInt()

            exerciseNumber.text = currentMessage
            Log.w("MainActivity", exerciseNumber.text.toString())
            currentExercise = Exercise(exerciseType, sets, reps)

            homepage.addToList(currentExercise) // probably add it to the journal as well

            beforeBreakScreen()
        }
    }

    fun beforeBreakScreen(){
        homepage.endBreak()
        homepage.startExercise()

        startScreenBottom.visibility = View.VISIBLE
        exerciseScreenBottom.visibility = View.GONE

//        motivationSpacer.visibility = View.GONE
        motivationMessage.visibility = View.VISIBLE

        questionPrompt.text = getString(R.string.before_break_message)
        currentMessage = getString(R.string.before_break_message)
    }

    fun pressX(){
        homepage.endExercise()
        homepage.endBreak()

//        motivationSpacer.visibility = View.VISIBLE
        motivationMessage.visibility = View.GONE

        startScreenBottom.visibility = View.VISIBLE
        exerciseScreenBottom.visibility = View.GONE
        questionPrompt.text = getString(R.string.end_message)
    }

    fun endExercise(){
//        motivationSpacer.visibility = View.VISIBLE
        motivationMessage.visibility = View.GONE

        questionPrompt.text = getString(R.string.start_message)
        currentMessage = getString(R.string.start_message)
        // reset the current exercise number
        homepage.resetExerciseNumber()
        // make sure to append the exercise into the journal
        homepage.clearArrayList()
    }

}


