package com.cg.weatherforecast

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.*
import android.widget.*
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import retrofit2.*
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CurrentWeather.newInstance] factory method to
 * create an instance of this fragment.
 */
class CurrentWeather : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pref = activity?.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val request = WeatherInterface.getInstance().getDaily(pref?.getString("Latitude","0.0000")!!,pref?.getString("longitude","0.0000")!!,resources.getString(R.string.apiKey),"metric")
        request.enqueue(DayCallback())
    }

    inner class DayCallback(): Callback<DailyList> {
        override fun onResponse(call: Call<DailyList>, response: Response<DailyList>) {
            if(response.isSuccessful){
                Log.d("Weather","Weather data: ${response.body()}")
                val temp = activity?.findViewById<TextView>(R.id.current_temperatureT)
                val pressure =  activity?.findViewById<TextView>(R.id.current_pressureT)
                val humid = activity?.findViewById<TextView>(R.id.current_humidityT)
                val feelsLike = activity?.findViewById<TextView>(R.id.curent_feelsT)
                val weather = activity?.findViewById<TextView>(R.id.current_typeT)
                val sunrise = activity?.findViewById<TextView>(R.id.current_sunriseT)
                val sunset = activity?.findViewById<TextView>(R.id.current_sunsetT)
                val wind = activity?.findViewById<TextView>(R.id.current_windT)
                val angle = activity?.findViewById<ImageView>(R.id.imageView4)
                val pBar = activity?.findViewById<ProgressBar>(R.id.progressBar)
                val details = response.body()
                CoroutineScope(Dispatchers.IO).launch{
                    delay(1500)
                    pBar?.visibility = View.INVISIBLE
                }

                val date1 = SimpleDateFormat("hh:mm a",
                    Locale.getDefault()).format(Date(details?.current?.sunrise!!*1000))
                val date2 = SimpleDateFormat("hh:mm a",
                    Locale.getDefault()).format(Date(details?.current?.sunset!!*1000))

                temp?.text = "${details?.current?.temp} ℃"
                weather?.text = "${details?.daily?.get(0)?.weather?.get(0)?.main}"
                pressure?.text = "Pressure - ${details?.current?.pressure} hPa"
                humid?.text = "Humidity - ${details?.current?.humidity}%"
                feelsLike?.text = "${details?.current?.feels_like}℃"
                wind?.text = "${details?.current?.wind_speed} m/s"
                sunrise?.text = "Sunrise at $date1"
                sunset?.text = "Sunset at $date2"
                val degrees = details?.current?.wind_deg
                angle?.rotation = degrees?.toFloat()!! - 90F
                var imagepath = details?.daily?.get(0)?.weather?.get(0)?.icon
                val imageUrl = "https://openweathermap.org/img/wn/$imagepath.png"
                val imageView = activity?.findViewById<ImageView>(R.id.imageView)
                if(!imagepath.isNullOrEmpty()) Glide.with(activity!!).load(Uri.parse(imageUrl!!)).into(imageView!!)


            }

        }

        override fun onFailure(call: Call<DailyList>, t: Throwable) {
            Toast.makeText(activity,"Wrong coordinates: ${t.message}", Toast.LENGTH_LONG).show()
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CurrentWeather.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CurrentWeather().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}