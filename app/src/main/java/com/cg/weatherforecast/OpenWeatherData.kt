package com.cg.weatherforecast



// =========================================


data class Daily(
    val dt:Long,
    val sunrise : Long,
    val sunset : Long,
    val temp : Temp,
    val feels_like: Feels_Like,
    val pressure : Double,
    val humidity: Double,
    val wind_speed : Double,
    val wind_deg: Double,
    val weather : List<Weather>

) {
    data class Temp(
        val min : Double,
        val max : Double,
        val day : Double
    )

    data class Feels_Like (
        val day : Double
    )
}

data class DailyList(
    val hourly : List<Hourly>,
    val daily : List<Daily>,
    val current : Current,
    val weather: List<Weather>
)

data class Hourly (
    val dt:Long,
    val temp : Double,
    val feels_like: Double,
    val pressure : Double,
    val humidity: Double,
    val wind_speed : Double,
    val wind_deg: Double,
    val weather : List<Weather>
)

data class Current (
    val sunrise : Long,
    val sunset : Long,
    val temp : Double,
    val feels_like: Double,
    val pressure : Double,
    val humidity: Double,
    val wind_speed : Double,
    val wind_deg: Double
)
data class Weather(
        val main : String,
        val icon : String
)
