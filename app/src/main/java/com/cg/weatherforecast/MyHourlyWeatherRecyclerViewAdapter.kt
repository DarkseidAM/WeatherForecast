package com.cg.weatherforecast

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*


class MyHourlyWeatherRecyclerViewAdapter(
    private val values: List<Hourly>
) : RecyclerView.Adapter<MyHourlyWeatherRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hourly_fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        val date = SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault()).format(Date(item.dt*1000))
        var imagePath = item.weather.get(0).icon
        val imageUrl = "https://openweathermap.org/img/wn/$imagePath@2x.png"

        if(!imagePath.isEmpty()) Glide.with(holder.itemView.context).load(Uri.parse(imageUrl)).into(holder.imageView)
        holder.date.text = date.toString()
        holder.max.text = "${item.temp} ℃"
        holder.desc.text = item.weather.get(0).main
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var date: TextView = view.findViewById(R.id.dateT)
        var max : TextView = view.findViewById(R.id.maxtempT)
        var desc : TextView = view.findViewById(R.id.descT)
        var imageView : ImageView = view.findViewById(R.id.ivrecycle)

    }
}