package com.cg.weatherforecast

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherInterface {

    @GET("onecall")
    fun getDaily(@Query("lat") lat: String,@Query("lon") lon: String,@Query("appid") appid:String,@Query("units") unit:String) : Call<DailyList>


    companion object{
        val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        fun getInstance() : WeatherInterface{
            val builder = Retrofit.Builder()
            builder.addConverterFactory(GsonConverterFactory.create())
            builder.baseUrl(BASE_URL)

            val retrofit = builder.build()
            return retrofit.create(WeatherInterface::class.java)

        }

    }

}

//