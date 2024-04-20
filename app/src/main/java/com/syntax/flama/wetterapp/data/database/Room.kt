package com.syntax.flama.wetterapp.data.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import com.syntax.flama.wetterapp.data.database.data.WeatherDataBase
import com.syntax.flama.wetterapp.data.database.data.WeatherFiveDayDataBase

@Dao
interface WeatherDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateWeather(data: WeatherDataBase)

    @Query("Select * from tb_weather")
    fun getDataWeather(): WeatherDataBase

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateFiveWeather(data: WeatherFiveDayDataBase)


    @Query("Select * from tb_weather_five_day")
    fun getDataFiveWeather(): WeatherFiveDayDataBase
}

@Database(
    entities = [WeatherDataBase::class, WeatherFiveDayDataBase::class],
    version = 1,
    exportSchema = false
)
abstract class DataBase : RoomDatabase() {
    abstract val weatherDao: WeatherDAO
}

