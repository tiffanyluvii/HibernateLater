package com.example.hibernatelater

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Timer

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        buildViewByCode()
    }

    fun animateBear(){

    }

    fun buildViewByCode(){
        setContentView(R.layout.activity_main)

        var timer: Timer = Timer()
        var task: ExerciseTimerTask = ExerciseTimerTask(this)
        timer.schedule(task, 0, 700)
    }

}


