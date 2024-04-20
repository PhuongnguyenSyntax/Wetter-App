package com.syntax.flama.wetterapp.data.database.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_weather")
data class WeatherDataBase(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: Int? = 0,
    @ColumnInfo(name = "location")
    var temp: Double? = 0.0,
    @ColumnInfo(name = "feels_like")
    var tempFeelsLike: Double? = 0.0,
    @ColumnInfo(name = "humidity")
    var humidity: Int? = 0,
    @ColumnInfo(name = "wind_speed")
    var windSpeed: Double? = 0.0,
    @ColumnInfo(name = "pressure")
    var pressure: Int? = 0,
    @ColumnInfo(name = "location_name")
    var location: String? = "",
    @ColumnInfo(name = "image_weather")
    var imageWeather: ByteArray? = null
)

@Entity(tableName = "tb_weather_five_day")
data class WeatherFiveDayDataBase(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: Int? = 0,
    @ColumnInfo(name = "list_five_day")
    var fiveDay: String? = ""
)