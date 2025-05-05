package com.example.hibernatelater

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.widget.CalendarView
import android.widget.CalendarView.OnDateChangeListener
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class CalendarActivity  : AppCompatActivity()  {
    private lateinit var calendar : CalendarView
    private lateinit var dateInfo : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        buildViewByCode()
    }

    private fun buildViewByCode() {
        calendar = findViewById(R.id.calendar)
        dateInfo = findViewById(R.id.date_info)
        var dateChangeListener = DateChangeListener()
        calendar.setOnDateChangeListener(dateChangeListener)
    }


    inner class DateChangeListener : OnDateChangeListener{
        override fun onSelectedDayChange(
            view: CalendarView,
            year: Int,
            month: Int,
            dayOfMonth: Int
        ) {
            Log.w("CalendarActivity", "$year, $month, $dayOfMonth")
            var ratingBar = RatingBar(this@CalendarActivity)
            ratingBar.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            ratingBar.numStars = 5
//            ratingBar.max = 5
            ratingBar.stepSize = 1.0f

            var ll = LinearLayout(this@CalendarActivity)
            ll.addView(ratingBar)
            ll.gravity = Gravity.CENTER
            ll.setVerticalGravity(Gravity.CENTER)

            var alertDialogBuilder : AlertDialog.Builder = AlertDialog.Builder(this@CalendarActivity)
            alertDialogBuilder.setTitle("Rate your workout on ${month+1}/$dayOfMonth/$year")
            var enterButton : Drawable = ContextCompat.getDrawable(this@CalendarActivity, R.drawable.enter_button)
            var noButton : Drawable = ContextCompat.getDrawable(this@CalendarActivity, R.drawable.no_button)
            alertDialogBuilder.setView(ll).setPositiveButton("") {_, _ ->
                var rating = ratingBar.rating
                updateInfoView(rating, year, month + 1, dayOfMonth)
            }
            alertDialogBuilder.setPositiveButtonIcon(enterButton)
            alertDialogBuilder.setNegativeButton("", null)
            alertDialogBuilder.setNegativeButtonIcon(noButton)
            alertDialogBuilder.show()
        }

        private fun updateInfoView(rating: Float, year: Int, month: Int, dayOfMonth: Int) {
            Log.w("CalendarActivity", "$rating")
            var color = COLORS[rating.toInt() - 1]
            dateInfo.setBackgroundColor(color)
            dateInfo.text = "$year, $month, $dayOfMonth"
        }

    }
    companion object {
        private var COLORS : Array<Int> = arrayOf(Color.parseColor("#57e32c"),
            Color.parseColor("#b7dd29"),
            Color.parseColor("#ffe234"),
            Color.parseColor("#ffa534"),
            Color.parseColor("#ff4545"))
    }

}
