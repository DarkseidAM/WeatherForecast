package com.cg.weatherforecast

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class SelectedDaily : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_daily)

        val intent = intent
        val arr = intent.getStringArrayExtra("data")!!

        val temp = findViewById<TextView>(R.id.current_temperatureT2)
        val pressure =  findViewById<TextView>(R.id.current_pressureT2)
        val humid = findViewById<TextView>(R.id.current_humidityT2)
        val feelsLike = findViewById<TextView>(R.id.curent_feelsT2)
        val weather = findViewById<TextView>(R.id.current_typeT2)
        val sunrise = findViewById<TextView>(R.id.current_sunriseT2)
        val sunset = findViewById<TextView>(R.id.current_sunsetT2)
        val wind = findViewById<TextView>(R.id.current_windT2)
        val angle = findViewById<ImageView>(R.id.imageView42)
        val dt = findViewById<TextView>(R.id.textView2)
        val imageView = findViewById<ImageView>(R.id.imageView2)

        val currDate =SimpleDateFormat("MMM dd, yyyy",
            Locale.getDefault()).format(Date(arr[0].toLong()*1000))
        val riseDate = SimpleDateFormat("hh:mm a",
            Locale.getDefault()).format(Date(arr[5].toLong()*1000))
        val setDate = SimpleDateFormat("hh:mm a",
            Locale.getDefault()).format(Date(arr[6].toLong()*1000))

        weather.text = arr[2]
        dt.text = currDate
        if(!arr[1].isNullOrEmpty()) Glide.with(this).load(Uri.parse(arr[1])).into(imageView)
        humid.text = "Humidity - ${arr[3]}%"
        pressure.text = "Pressure - ${arr[4]} hPa"
        temp.text = "${arr[9]} ℃"
        feelsLike.text = "${arr[10]} ℃"
        sunrise.text = "Sunrise at $riseDate"
        sunset.text = "Sunset at $setDate"
        wind.text = "${arr[8]} m/s"
        angle.rotation = arr[7].toFloat() - 90F

    }
}
//11