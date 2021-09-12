package com.example.pogodynka

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private var CITY:String = "Warszawa"
    private val API:String = "64e3f184f021b64efaafa8ae7f016849"

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        WeatherTask().execute()

        // Declare the switch from the layout file
        val btn = findViewById<Switch>(R.id.switchtheme)

        // set the switch to listen on checked change
        btn.setOnCheckedChangeListener { _, isChecked ->

            // if the button is checked, i.e., towards the right or enabled
            // enable dark mode, change the text to disable dark mode
            // else keep the switch text to enable dark mode
            if (btn.isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                btn.text = "Jasny motyw"
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                btn.text = "Ciemny motyw"
            }
        }


        imageButtonSearch.setOnClickListener {
            CITY = editTextCity.text.toString()
            WeatherTask().execute()

        }
    }




    inner class WeatherTask() : AsyncTask<String, Any, String>()
    {

        override fun onPreExecute() {
            super.onPreExecute()
        }


        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API&lang=pl").readText(
                    Charsets.UTF_8
                )
            }catch (e: Exception){
                response = null
            }
            return response
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            try {
                super.onPostExecute(result)
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val temp = main.getString("temp").toDouble().roundToInt().toString() + "°C"
                val pogoda = jsonObj.getJSONArray("weather").getJSONObject(0)

                val cisnienie = main.getString("pressure") + " hPa"

                val sunrise: Long = sys.getLong("sunrise")
                val sunset: Long = sys.getLong("sunset")
                val opis = pogoda.getString("description")

                val miasto = jsonObj.getString("name") + ", " + sys.getString("country")

                findViewById<TextView>(R.id.textViewCity).text = miasto
                findViewById<TextView>(R.id.textViewDegree).text = temp
                findViewById<TextView>(R.id.textViewWeather).text = opis
                findViewById<TextView>(R.id.textViewPressure).text = cisnienie
                findViewById<TextView>(R.id.textViewsunrise).text = SimpleDateFormat("HH:mm").format(Date(sunrise * 1000))
                findViewById<TextView>(R.id.textViewSunset).text = SimpleDateFormat("HH:mm").format(Date(sunset * 1000))
                findViewById<ImageView>(R.id.imageViewSunrise).setImageResource(R.drawable.sunrise)
                findViewById<ImageView>(R.id.imageViewSunset).setImageResource(R.drawable.sunset)
                findViewById<ImageView>(R.id.imageViewPressure).setImageResource(R.drawable.barometr)
                findViewById<TextView>(R.id.textViewDayAndTime).text = "Stan na: "+ SimpleDateFormat("dd.MM.yyyy HH:mm").format(Date())


                setIcon(pogoda.getString("icon"))



            }
            catch (e:Exception){
            }
        }
    }


    private fun setIcon(icon: String)
    {
        var imageView= findViewById<ImageView>(R.id.imageViewWeather)

        when (icon) {
            "01d" -> imageView.setImageResource(R.drawable.sun)
            "02d" -> imageView.setImageResource(R.drawable.cloudy)
            "03d" -> imageView.setImageResource(R.drawable.cloud)
            "04d" -> imageView.setImageResource(R.drawable.cloud)
            "09d" -> imageView.setImageResource(R.drawable.rain)
            "10d" -> imageView.setImageResource(R.drawable.rainy)
            "11d" -> imageView.setImageResource(R.drawable.storm)
            "13d" -> imageView.setImageResource(R.drawable.snow)
            "50d" -> imageView.setImageResource(R.drawable.mist)
            "01n" -> imageView.setImageResource(R.drawable.sun)
            "02n" -> imageView.setImageResource(R.drawable.cloudy)
            "03n" -> imageView.setImageResource(R.drawable.cloud)
            "04n" -> imageView.setImageResource(R.drawable.cloud)
            "09n" -> imageView.setImageResource(R.drawable.rain)
            "10n" -> imageView.setImageResource(R.drawable.rainy)
            "11n" -> imageView.setImageResource(R.drawable.storm)
            "13n" -> imageView.setImageResource(R.drawable.snow)
            "50n" -> imageView.setImageResource(R.drawable.mist)
        }
    }
}