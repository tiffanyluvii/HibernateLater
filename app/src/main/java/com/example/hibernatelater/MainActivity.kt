package com.example.hibernatelater

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Timer

class MainActivity : AppCompatActivity() {
    private lateinit var bear_icon: View
    private var check_exercise: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        buildViewByCode()
    }

    fun animateBear(){
        if (!check_exercise){
            bear_icon.setBackgroundResource(R.drawable.dumbbell_bear)
        } else {
            bear_icon.setBackgroundResource(R.drawable.dumbbell_bear_up)
        }
        check_exercise = !check_exercise

    }

    fun buildViewByCode(){
        setContentView(R.layout.activity_main)

        bear_icon = findViewById(R.id.bear)
        var timer: Timer = Timer()
        var task: ExerciseTimerTask = ExerciseTimerTask(this)
        timer.schedule(task, 0, 700)
    }

}


