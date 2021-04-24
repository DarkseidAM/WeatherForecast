package com.cg.weatherforecast

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import android.view.*
import android.widget.Toast
import kotlinx.coroutines.*
import retrofit2.*

/**
 * A fragment representing a list of Items.
 */
class DailyForecastFragment : Fragment() {

    private var columnCount = 1
    var type : DailyList? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_forecast_list, container, false)


        // Set the adapter
        CoroutineScope(Dispatchers.Main).launch{
            val j = CoroutineScope(Dispatchers.Default).launch {
                val pref = activity?.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                val request = WeatherInterface.getInstance().getDaily(pref?.getString("Latitude","0.0000")!!,pref?.getString("longitude","0.0000")!!,resources.getString(R.string.apiKey),"metric")
                request.enqueue(DailyCallback())
            }
            j.join()
            delay(500)
            if (view is RecyclerView) {
                with(view) {
                    layoutManager = when {
                        columnCount <= 1 -> LinearLayoutManager(context)
                        else -> GridLayoutManager(context, columnCount)
                    }
                    try {
                        Log.d("AdapterAc","$type")
                        adapter = MyForecastRecyclerViewAdapter(type?.daily!!)
                    } catch (e: Exception) {
                        Log.d("Forecast", "${e.message}")
                    }
                }
            }

        }

        return view
    }

    inner class DailyCallback : Callback<DailyList>{
        override fun onResponse(call: Call<DailyList>, response: Response<DailyList>) {
            if(response.isSuccessful){
                type = response.body()
                Log.d("Forecast","Data- ${response.body()}")
                //List = List<type>
            }
        }

        override fun onFailure(call: Call<DailyList>, t: Throwable) {
            Toast.makeText(activity,"Error: ${t.message}",Toast.LENGTH_LONG).show()
        }

    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            DailyForecastFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}