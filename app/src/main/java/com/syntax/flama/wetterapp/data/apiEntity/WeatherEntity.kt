package com.syntax.flama.wetterapp.data.apiEntity

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class WeatherFiveDayEntity(
    val city: City? = null,
    val cnt: Int? = 0,
    val cod: String? = "",
    val list: List<WeatherEntity>,
    val message: Int? = 0
)

data class City(
    val coord: Coord? = null,
    val country: String? = "",
    val id: Int? = 0,
    val name: String? = "",
    val population: Int? = 0,
    val sunrise: Int? = 0,
    val sunset: Int? = 0,
    val timezone: Int? = 0
)

@Keep
@JsonClass(generateAdapter = true)
data class WeatherEntity(
    @Json(name = "base")
    var base: String? = "",
    @Json(name = "clouds")
    var clouds: Clouds? = null,
    @Json(name = "cod")
    var cod: Int? = 0,
    @Json(name = "coord")
    var coord: Coord? = null,
    @Json(name = "dt")
    var dt: Int? = 0,
    @Json(name = "id")
    var id: Int? = 0,
    @Json(name = "main")
    var main: Main,
    @Json(name = "name")
    var name: String? = "",
    @Json(name = "sys")
    var sys: Sys? = null,
    @Json(name = "timezone")
    var timezone: Int? = 0,
    @Json(name = "visibility")
    var visibility: Int? = 0,
    @Json(name = "weather")
    var weather: List<Weather>,
    @Json(name = "wind")
    var wind: Wind,
    @Json(name = "dt_txt")
    var dt_txt : String? = ""
)

@Keep
@JsonClass(generateAdapter = true)
data class Clouds(
    @Json(name = "all")
    var all: Int
)

@Keep
@JsonClass(generateAdapter = true)
data class Coord(
    @Json(name = "lat")
    var lat: Double,
    @Json(name = "lon")
    var lon: Double
)

@Keep
@JsonClass(generateAdapter = true)
data class Main(
    @Json(name = "feels_like")
    var feelsLike: Double,
    @Json(name = "grnd_level")
    var grndLevel: Int? = 0,
    @Json(name = "humidity")
    var humidity: Int? = 0,
    @Json(name = "pressure")
    var pressure: Int,
    @Json(name = "sea_level")
    var seaLevel: Int? = 0,
    @Json(name = "temp")
    var temp: Double? = 0.0,
    @Json(name = "temp_max")
    var tempMax: Double? = 0.0,
    @Json(name = "temp_min")
    var tempMin: Double? = 0.0
)

@Keep
@JsonClass(generateAdapter = true)
data class Sys(
    @Json(name = "country")
    var country: String? = "",
    @Json(name = "id")
    var id: Int? = 0,
    @Json(name = "sunrise")
    var sunrise: Int? =0,
    @Json(name = "sunset")
    var sunset: Int? =0,
    @Json(name = "type")
    var type: Int? =0
)

@Keep
@JsonClass(generateAdapter = true)
data class Weather(
    @Json(name = "description")
    var description: String,
    @Json(name = "icon")
    var icon: String,
    @Json(name = "id")
    var id: Int,
    @Json(name = "main")
    var main: String
)

@Keep
@JsonClass(generateAdapter = true)
data class Wind(
    @Json(name = "deg")
    var deg: Int? = 0,
    @Json(name = "gust")
    var gust: Double? = 0.0,
    @Json(name = "speed")
    var speed: Double? = 0.0
)