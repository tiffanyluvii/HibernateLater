package com.example.hibernatelater

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.widget.CalendarView
import android.widget.CalendarView.OnDateChangeListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import java.util.Calendar

class CalendarActivity  : AppCompatActivity()  {
    private lateinit var calendar : CalendarView
    private lateinit var dateInfo : TextView
    private lateinit var starInfo : LinearLayout
    private lateinit var adView: AdView
    private lateinit var adViewLL: LinearLayout
    private lateinit var backButton : AppCompatButton
    private lateinit var editRating : AppCompatButton
    private lateinit var journal : AppCompatButton
    private var adUnitId : String = "ca-app-pub-3940256099942544/6300978111"
    private lateinit var pref : SharedPreferences;
    private var calendarInstance = Calendar.getInstance()
    private var selectedYear = 0
    private var selectedMonth = 0
    private var selectedDay = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        pref = this.getSharedPreferences( this.packageName + "_preferences", Context.MODE_PRIVATE )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        buildViewByCode()
    }

    private fun buildViewByCode() {
        calendarInstance = Calendar.getInstance()
        selectedYear = calendarInstance.get(Calendar.YEAR)
        selectedMonth = calendarInstance.get(Calendar.MONTH)
        selectedDay = calendarInstance.get(Calendar.DAY_OF_MONTH)
        calendar = findViewById(R.id.calendar)
        dateInfo = findViewById(R.id.date_info)
        starInfo = findViewById(R.id.star_info)

        var dateChangeListener = DateChangeListener()
        calendar.setOnDateChangeListener(dateChangeListener)

        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener{goBack()}

        editRating = findViewById(R.id.change_rating_button)
        editRating.setOnClickListener{
            showRatingDialog(selectedYear, selectedMonth, selectedDay)}

        journal = findViewById(R.id.journal_icon)
        journal.setOnClickListener{goToJournal()}

        // load up rating on start after everything is loaded in
        val defaultRating = pref.getFloat("$selectedYear, ${selectedMonth+1}, $selectedDay", 0F)
        updateInfoView(defaultRating, selectedYear, selectedMonth, selectedDay)
        updateStars(defaultRating)
        Log.w("CalendarActivity", "selecteds: $selectedYear, $selectedMonth, $selectedDay")

        adView = AdView(this)
        var adSize = AdSize(AdSize.FULL_WIDTH, AdSize.AUTO_HEIGHT)
        adView.setAdSize(adSize)
        adView.adUnitId = adUnitId

        // create ad request
        var builder : AdRequest.Builder = AdRequest.Builder()
        var request : AdRequest = builder.build()

        // place adView in LinearLayout
        var adLayout : LinearLayout = findViewById(R.id.ad_view_ll)
        adLayout.addView(adView)

        // request ad from google
        adView.loadAd(request)
    }

    private fun goToJournal() {
        val intent = Intent(this, JournalActivity::class.java)
        startActivity(intent)
    }

    private fun goBack() {
        this.finishAfterTransition()
        finish()
    }

    private fun showRatingDialog(
        year: Int,
        month: Int,
        dayOfMonth: Int
        ) {
            Log.w("CalendarActivity", "$year, $month, $dayOfMonth")
            val stars = pref.getFloat("$year, ${month + 1}, $dayOfMonth", 0F)


                //make rating bar
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
                var enterButton : Drawable? = ContextCompat.getDrawable(this@CalendarActivity, R.drawable.enter_button)
                var noButton : Drawable? = ContextCompat.getDrawable(this@CalendarActivity, R.drawable.no_button)
                alertDialogBuilder.setView(ll).setPositiveButton("") {_, _ ->
                    var rating = ratingBar.rating
                    var editor : SharedPreferences.Editor = pref.edit()
                    editor.putFloat("$year, ${month + 1}, $dayOfMonth", rating)
                    editor.commit()
                    updateInfoView(rating, year, month, dayOfMonth)
                    updateStars(rating)
                }
                alertDialogBuilder.setPositiveButtonIcon(enterButton)
                alertDialogBuilder.setNegativeButton("", null)
                alertDialogBuilder.setNegativeButtonIcon(noButton)
                alertDialogBuilder.show()
    }

    private fun updateStars(rating: Float) {
        var starId = STARS[rating.toInt() - 1]
        starInfo.removeAllViews()
        for (i in 0..<rating.toInt()){
            val imageView = ImageView(this@CalendarActivity)
            imageView.setImageResource(starId)
            val params = LinearLayout.LayoutParams(200, 200)
//                params.setMargins(8, 0, 8, 0)
            imageView.layoutParams = params
            starInfo.addView(imageView)
        }
    }

    private fun updateInfoView(rating: Float, year: Int, month: Int, dayOfMonth: Int) {
        Log.w("CalendarActivity", "$rating")
        when (rating){
            in (1.0..5.0) -> {
                var color = COLORS[rating.toInt() - 1]
                dateInfo.setBackgroundColor(color)
            }
        }
        dateInfo.text="${month+1}/$dayOfMonth/$year"
    }


    inner class DateChangeListener : OnDateChangeListener{
        override fun onSelectedDayChange(
            view: CalendarView,
            year: Int,
            month: Int,
            dayOfMonth: Int
        ) {
            Log.w("CalendarActivity", "calendarChosed: $year, $month, $dayOfMonth")
            selectedYear = year
            selectedMonth = month
            selectedDay = dayOfMonth
            val stars = pref.getFloat("$year, ${month + 1}, $dayOfMonth", 0F)

            // if not rated yet
            if (stars == 0F){
                this@CalendarActivity.showRatingDialog(year, month, dayOfMonth)
            } else {
                this@CalendarActivity.updateInfoView(stars, year, month, dayOfMonth)
                this@CalendarActivity.updateStars(stars)
            }

        }
    }

    companion object {
        private var COLORS : Array<Int> = arrayOf(Color.parseColor("#ff4545"),
            Color.parseColor("#ffa534"),
            Color.parseColor("#ffe234"),
            Color.parseColor("#b7dd29"),
            Color.parseColor("#57e32c"))
        private var STARS = arrayOf(R.drawable.red_star, R.drawable.orange_star,
            R.drawable.yellow_star,
            R.drawable.light_green_star,
            R.drawable.green_star)
    }

}
