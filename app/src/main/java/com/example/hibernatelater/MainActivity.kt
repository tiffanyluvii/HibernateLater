package com.example.hibernatelater

import android.app.ActivityOptions
import android.content.Intent
import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Timer

class MainActivity : AppCompatActivity() {
    private var streak : Int = 0

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
    private lateinit var enterTimerButton: AppCompatButton
    private lateinit var motivationSpacer: View
    private lateinit var motivationMessage: TextView

    private lateinit var exerciseInput: EditText
    private lateinit var setInput: EditText
    private lateinit var repInput: EditText

    private lateinit var minuteInput: EditText
    private lateinit var secondInput: EditText
    private lateinit var timer: TextView

    private lateinit var currentExercise: Exercise

    private lateinit var startScreenBottom: LinearLayout
    private lateinit var exerciseScreenBottom: LinearLayout
    private lateinit var timerScreenBottom: LinearLayout
    private lateinit var player: MediaPlayer

    private lateinit var leaderboardScreen: LinearLayout
    private lateinit var leaderboard: TextView
    private lateinit var streakView: ListView

    private var checkExercise: Boolean = false
    private var checkBreak: Int = 0
    private var checkTimer: Boolean = false
    private var checkTimerView: Boolean = false
    private lateinit var currentMessage: String


    private lateinit var homepage: HomePage
    private lateinit var leadadapt : Adapter
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
        } else if (homepage.currentlyOnBreak()){
            if (checkBreak == 0){
                bearIcon.setBackgroundResource(R.drawable.sleeping_bear4)
            } else if (checkBreak == 1){
                bearIcon.setBackgroundResource(R.drawable.sleeping_bear)
            } else if (checkBreak == 2){
                bearIcon.setBackgroundResource(R.drawable.sleeping_bear2)
            } else if (checkBreak == 3){
                bearIcon.setBackgroundResource(R.drawable.sleeping_bear3)
            }
            checkBreak++
        } else {
            // change this to a sleeping bear later
            bearIcon.setBackgroundResource(R.drawable.regular_bear)
        }
        checkExercise = !checkExercise
        checkBreak %= 4
    }

    fun buildViewByCode() {
        var firebase: FirebaseDatabase = FirebaseDatabase.getInstance()
        var listener: DataListener = DataListener()

        setContentView(R.layout.activity_main)

        bearIcon = findViewById(R.id.bear)
        yesButton = findViewById(R.id.yes_button)
        noButton = findViewById(R.id.no_button)
        questionPrompt = findViewById(R.id.question)

        xButton = findViewById(R.id.x_icon)
        awardButton = findViewById(R.id.award_icon)
        journalButton = findViewById(R.id.journal_icon)
        calendarButton = findViewById(R.id.calendar_icon)
        calendarButton.setOnClickListener{goToCalendar()}

        startScreenBottom = findViewById(R.id.bottomHalfStartScreen)
        exerciseScreenBottom = findViewById(R.id.bottomHalfWorkoutScreen)
        timerScreenBottom = findViewById(R.id.bottomHalfTimerScreen)


        leaderboardScreen = findViewById(R.id.leaderboardScreen)
        leaderboard = findViewById(R.id.leaderboard)
        streakView = findViewById(R.id.streakList)



        exerciseNumber = findViewById(R.id.exerciseQuestion)
        enterButton = findViewById(R.id.enterButton)
        exerciseInput = findViewById(R.id.exerciseInput)
        setInput = findViewById(R.id.setsInput)
        repInput = findViewById(R.id.repsInput)

        enterTimerButton = findViewById(R.id.enterTimerButton)
        minuteInput = findViewById(R.id.minuteInput)
        secondInput = findViewById(R.id.secondInput)
        timer = findViewById(R.id.timer)

        motivationMessage = findViewById(R.id.motivation)
//        motivationSpacer = findViewById(R.id.motivationSpacer)

        currentMessage = getString(R.string.start_message)

        homepage = HomePage(this)

        player = MediaPlayer.create(this, R.raw.alarm)

        yesButton.setOnClickListener{clickYes()}
        noButton.setOnClickListener{clickNo()}
        enterButton.setOnClickListener{enterExercise()}
        enterTimerButton.setOnClickListener{enterTimer()}
        xButton.setOnClickListener{pressX()}
        awardButton.setOnClickListener { pressAward() }
        journalButton.setOnClickListener{ displayJournal() }


        var timer: Timer = Timer()
        var task: ExerciseTimerTask = ExerciseTimerTask(this)
        timer.schedule(task, 0, 700)
    }

    private fun goToCalendar() {
        var intent: Intent = Intent(this, CalendarActivity::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
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
            currentExercise.incrementSet()
            beforeBreakScreen()

        } else if (currentPrompt == getString(R.string.after_break_message2)){
            //                currentExercise.resetSets() you can't reset it but idk if it's necessarily
            startWorkout()
        }

    }


    fun clickNo(){
        // the no button will be reused so check the current prompt to determine what to do
        var currentPrompt = this.questionPrompt.text.toString()

        if (currentPrompt == getString(R.string.after_break_message)
            || currentPrompt == getString(R.string.after_break_message2)){
            // rerun the timer
            startBreak()

        } else if (currentPrompt == getString(R.string.end_message)){
            // add one for the timer view

            // go back to the most recent view
            if (checkTimerView){
                Log.w("MainActivity", "checkTimerView")
                checkTimerView = true
                homepage.startBreak()
                startScreenBottom.visibility = View.GONE
                timerScreenBottom.visibility = View.VISIBLE

            } else if (currentMessage == exerciseNumber.text.toString()){
                // check if they were in the exercise screen
                homepage.endBreak()
                homepage.startExercise()
                startScreenBottom.visibility = View.GONE
                exerciseScreenBottom.visibility = View.VISIBLE
            } else if (currentMessage == getString(R.string.start_message)){
                questionPrompt.text = getString(R.string.start_message)
                currentMessage = getString(R.string.start_message)
            } else if (currentMessage == getString(R.string.before_break_message)){

                motivationMessage.visibility = View.VISIBLE

                homepage.endBreak()
                homepage.startExercise()
                questionPrompt.text = getString(R.string.before_break_message)
                currentMessage = getString(R.string.before_break_message)

            } else if (currentMessage == getString(R.string.after_break_message) ||
                currentMessage == getString(R.string.after_break_message2)){

                questionPrompt.text = getString(R.string.after_break_message)
                currentMessage = getString(R.string.after_break_message)
            }
        }
    }


    fun startWorkout(){
        homepage.endBreak()

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

        checkTimerView = true

//        motivationSpacer.visibility = View.GONE
        motivationMessage.visibility = View.GONE

        // display the timer view
        startScreenBottom.visibility = View.GONE
        timerScreenBottom.visibility = View.VISIBLE

    }

    fun timerEnded(){
        homepage.endBreak()
        checkTimerView = false

        timerScreenBottom.visibility = View.GONE
        startScreenBottom.visibility = View.VISIBLE

        if (currentExercise.finishedExercise()){
            questionPrompt.text = getString(R.string.after_break_message2)
            currentMessage = getString(R.string.after_break_message2)
        } else {
            questionPrompt.text = getString(R.string.after_break_message)
            currentMessage = getString(R.string.after_break_message)
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


            // PERSISTENT DATA MANAGEMENT
            val currentDate = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())

            // Create an ExerciseEntity object with the data
            val exercise = ExerciseEntity(
                name = exerciseType,
                sets = sets,
                reps = reps,
                date = currentDate
            )

            // Save the exercise in the database (Room)
            val db = AppDatabase.getDatabase(this)
            val dao = db.exerciseDao()

            lifecycleScope.launch {
                dao.insert(exercise)
            }

            // END OF PERSISTENT DATA CODE

            beforeBreakScreen()
        }
    }




    fun enterTimer(){
        var minute: Int = 0
        var second: Int = 0

        Log.w("MainActivity", "runTimer")

        if (minuteInput.text.toString() != ""){
            minute = minuteInput.text.toString().toInt()
        }

        if (secondInput.text.toString() != ""){
            second = secondInput.text.toString().toInt()
        }

        if (!checkTimer){
            minute *= 60000
            second *= 1000
            runTimer((minute + second).toLong())
            checkTimer = true
        }
    }

    fun beforeBreakScreen(){
        homepage.endBreak()
        homepage.startExercise()

        startScreenBottom.visibility = View.VISIBLE
        exerciseScreenBottom.visibility = View.GONE

//        motivationSpacer.visibility = View.GONE
        motivationMessage.text =
            getString(R.string.motivation_message) + "\n" +
                    "Set " + currentExercise.getCurrentSet() + ": " +
                    currentExercise.getReps() + " rep(s)" +
                    " of " + currentExercise.getExerciseType() + "!"
        motivationMessage.visibility = View.VISIBLE

        questionPrompt.text = getString(R.string.before_break_message)
        currentMessage = getString(R.string.before_break_message)
    }

    fun pressX(){
        if (leaderboardScreen.visibility == View.VISIBLE){
            questionPrompt.text = getString(R.string.start_message)
            currentMessage = getString(R.string.start_message)
        } else {
            questionPrompt.text = getString(R.string.end_message)
            currentMessage = getString(R.string.end_message)
        }

        homepage.endExercise()
        homepage.endBreak()

//        motivationSpacer.visibility = View.VISIBLE
        motivationMessage.visibility = View.GONE

        startScreenBottom.visibility = View.VISIBLE
        exerciseScreenBottom.visibility = View.GONE
        timerScreenBottom.visibility = View.GONE
        leaderboardScreen.visibility = View.GONE
        yesButton.visibility = View.VISIBLE
        noButton.visibility = View.VISIBLE
    }

    fun pressAward() {
        streakView.adapter = Adapter(this, getStreakData())
        homepage.endExercise()
        homepage.endBreak()

        leaderboardScreen.visibility = View.VISIBLE

        motivationMessage.visibility = View.GONE
        startScreenBottom.visibility = View.GONE
        exerciseScreenBottom.visibility = View.GONE
        yesButton.visibility = View.GONE
        noButton.visibility = View.GONE



    }


    fun endExercise() {
//        motivationSpacer.visibility = View.VISIBLE
        motivationMessage.visibility = View.GONE

        questionPrompt.text = getString(R.string.start_message)
        currentMessage = getString(R.string.start_message)
        // reset the current exercise number
        homepage.resetExerciseNumber()
        // make sure to append the exercise into the journal
        homepage.clearArrayList()
    }

    fun displayJournal() {
        val intent = Intent(this, JournalActivity::class.java)
        startActivity(intent)
    }


    fun runTimer(millisUntilFinished: Long){
        var breakTimer: BreakTimer = BreakTimer(millisUntilFinished, 1000)
        breakTimer.start()
    }

    inner class BreakTimer(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {
        override fun onTick(millisUntilFinished: Long) {
            var format: NumberFormat = DecimalFormat("00");
            var min: Long = (millisUntilFinished / 60000) % 60;
            var sec: Long = (millisUntilFinished / 1000) % 60;
            timer.setText(format.format(min) + ":" + format.format(sec))
        }

        override fun onFinish() {
            timer.setText("00:00")
            checkTimer = false
            player.setVolume(1f, 1f)
            player.start()
            Thread.sleep(500)
            timerEnded()
        }
    }

    inner class DataListener : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.value != null) {
                Log.w("MainActivity", "new value is " + snapshot.value.toString())
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("MainActivity", "error")
        }
    }


    //testing class
    data class User(
        val name: String,
        val streakDays: Int
    ) {
        companion object {
            val sortByStreak: Comparator<User> = compareByDescending { it.streakDays }
        }
    }

    // test set
    private fun getStreakData(): List<User> {
        return listOf(
            User("Eli", 12),
            User("Tiffany", 8),
            User("Ryan", 5),
            User("Grace", 22),
            User("You", 0)
        ).sortedWith(User.sortByStreak) // sort highest
    }

}


