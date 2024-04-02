package com.example.weatherapp

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.widget.addTextChangedListener
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {
    private lateinit var editText_city: EditText;
    private lateinit var button_search: Button;
    private lateinit var textView_temperature: TextView;
    private lateinit var textView_wind_speed: TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        editText_city = findViewById(R.id.editText_city);
        button_search = findViewById(R.id.button_search);
        textView_temperature = findViewById(R.id.textView_temperature);
        textView_wind_speed = findViewById(R.id.textView_wind_speed);

        button_search.isEnabled = false;

        editText_city.addTextChangedListener { checkCity() }

        button_search.setOnClickListener { searchWeather() }
    }

    private fun checkCity() {
        val city = editText_city.text.toString()

        button_search.isEnabled = city.isNotBlank()
    }

    private fun searchWeather() {
        val city = editText_city.text.toString()
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        editText_city.isEnabled = false
        button_search.isEnabled = false

        thread {
            try {
                val url = URL("https://goweather.herokuapp.com/weather/$city")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(response)

                    val temperature = jsonResponse.optString("temperature")
                    val windSpeed = jsonResponse.optString("wind")

                    runOnUiThread {
                        textView_temperature.text = "Temperatura: $temperature"
                        textView_wind_speed.text = "Velocidade do Vento: $windSpeed"
                    }
                } else {
                    runOnUiThread {
                        textView_temperature.text = "Falha ao obter dados meteorológicos."
                        textView_wind_speed.text = ""
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    textView_temperature.text = "Erro ao obter dados meteorológicos."
                    textView_wind_speed.text = ""
                }
            } finally {
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    editText_city.isEnabled = true
                    button_search.isEnabled = true
                }
            }
        }
    }


}