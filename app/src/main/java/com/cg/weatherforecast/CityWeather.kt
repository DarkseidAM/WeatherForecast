package com.cg.weatherforecast

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.*
import android.widget.*
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import retrofit2.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CityWeather.newInstance] factory method to
 * create an instance of this fragment.
 */
class CityWeather : Fragment() {
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
        return inflater.inflate(R.layout.fragment_city_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pref = activity?.getSharedPreferences(PREF_NAME,MODE_PRIVATE)
        val request = WeatherInterface.getInstance().getDaily(pref?.getString("Latitude","0.0000")!!,pref?.getString("longitude","0.0000")!!,resources.getString(R.string.apiKey),"metric")
        request.enqueue(DayCallback())

        val button = activity?.findViewById<Button>(R.id.nextB)
        button?.setOnClickListener{
//            val frag = ForecastFragment()
//
//            activity?.supportFragmentManager
//                ?.beginTransaction()
//                ?.replace(R.id.layoutL,frag)
//                ?.addToBackStack(null)
//                ?.commit()
            startActivity(Intent(view.context,MoreDetailsActivity::class.java))
        }

    }

    inner class DayCallback(): Callback<DailyList> {
        override fun onResponse(call: Call<DailyList>, response: Response<DailyList>) {
                if(response.isSuccessful){
                    val temp = activity?.findViewById<TextView>(R.id.tempT)
                    val humid = activity?.findViewById<TextView>(R.id.humidT)
                    val feelsLike = activity?.findViewById<TextView>(R.id.feelsT)
                    val weather = activity?.findViewById<TextView>(R.id.weatherT)
                    val max = activity?.findViewById<TextView>(R.id.maxT)
                    val min = activity?.findViewById<TextView>(R.id.minT)
                    val pBar = activity?.findViewById<ProgressBar>(R.id.progressBar)
                    val details = response.body()
                    CoroutineScope(Dispatchers.IO).launch{
                        delay(1500)
                        pBar?.visibility = View.INVISIBLE
                    }
                    temp?.text = "${details?.current?.temp} ℃"
                    weather?.text = "${details?.daily?.get(0)?.weather?.get(0)?.main}"
                    max?.text = "${details?.daily?.get(0)?.temp?.max} ℃"
                    min?.text = "${details?.daily?.get(0)?.temp?.min} ℃"
                    humid?.text = "Humidity  ${details?.current?.humidity}%"
                    feelsLike?.text = "Feels like ${details?.current?.feels_like}℃"
                    var imagepath = details?.daily?.get(0)?.weather?.get(0)?.icon
                    val imageUrl = "https://openweathermap.org/img/wn/$imagepath@2x.png"
                    val imageView = activity?.findViewById<ImageView>(R.id.iv1)
                    Log.d("Weather","Weather data: ${imageUrl}")
                    //if(!imagepath.isNullOrEmpty()) Glide.with(activity!!).load(Uri.parse(imageUrl)).into(imageView!!)

                }

        }

        override fun onFailure(call: Call<DailyList>, t: Throwable) {
            Toast.makeText(activity,"Wrong coordinates: ${t.message}",Toast.LENGTH_LONG).show()
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CityWeather.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CityWeather().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}