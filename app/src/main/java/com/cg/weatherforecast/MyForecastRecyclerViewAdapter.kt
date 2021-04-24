package com.cg.weatherforecast

import android.content.Intent
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*



class MyForecastRecyclerViewAdapter(
    private val values: List<Daily>
) : RecyclerView.Adapter<MyForecastRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_forecast, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = values[position]
        val date = SimpleDateFormat("MMM dd, E",Locale.getDefault()).format(Date(item.dt*1000))
        var imagePath = item.weather.get(0).icon
        val imageUrl = "https://openweathermap.org/img/wn/$imagePath@2x.png"

        if(!imagePath.isEmpty()) Glide.with(holder.itemView.context).load(Uri.parse(imageUrl)).into(holder.imageView)
        holder.date.text = date.toString()
        holder.min.text = "${item.temp.min} ℃"
        holder.max.text = "${item.temp.max} ℃"
        holder.desc.text = item.weather.get(0).main
        val arr = arrayOf<String>(item.dt.toString(),imageUrl,item.weather.get(0).main,item.humidity.toString(),item.pressure.toString(),item.sunrise.toString(),item.sunset.toString(),item.wind_deg.toString(),item.wind_speed.toString(),item.temp.day.toString(),item.feels_like.day.toString())
        holder.itemView.setOnClickListener{
            val i = Intent(it.context,SelectedDaily::class.java)
            i.putExtra("data",arr)
            it.context.startActivity(i)
        }

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var date: TextView = view.findViewById(R.id.dateT2)
        var min: TextView = view.findViewById(R.id.mintempT2)
        var max : TextView = view.findViewById(R.id.maxtempT2)
        var desc : TextView = view.findViewById(R.id.descT2)
        var imageView : ImageView = view.findViewById(R.id.ivrecycle2)

    }
}