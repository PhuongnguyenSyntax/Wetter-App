package com.syntax.flama.wetterapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.syntax.flama.wetterapp.BuildConfig
import com.syntax.flama.wetterapp.R
import com.syntax.flama.wetterapp.ViewModel.MainViewModel
import com.syntax.flama.wetterapp.data.apiEntity.WeatherEntity
import com.syntax.flama.wetterapp.data.database.data.WeatherDataBase
import com.syntax.flama.wetterapp.data.database.data.WeatherFiveDayDataBase
import com.syntax.flama.wetterapp.data.liveData.StateData
import com.syntax.flama.wetterapp.databinding.ActivityMainBinding
import com.syntax.flama.wetterapp.ui.base.BaseActivity
import com.syntax.flama.wetterapp.util.Constants
import com.syntax.flama.wetterapp.util.Constants.hpaExtensions
import com.syntax.flama.wetterapp.util.Constants.percentExtensions
import com.syntax.flama.wetterapp.util.Constants.tempExtensionC
import com.syntax.flama.wetterapp.util.Constants.tempExtensionF
import com.syntax.flama.wetterapp.util.Constants.windExtensionF
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var temple: Double = 0.0
    private var templeFeelLike: Double = 0.0
    private var isC = true
    private var seeFiveDayAdapter: SeeFiveDayAdapter? = null
    private var arrWeatherDay = mutableListOf<WeatherEntity>()
    private val simpleDateFormatInput = SimpleDateFormat("MM-yy-dd HH:mm:ss")
    private val simpleDateFormatOutput = SimpleDateFormat("MM-yy-dd")

    override fun getLayoutActivity() = R.layout.activity_main

    override fun initViews() {
        super.initViews()

        seeFiveDayAdapter = SeeFiveDayAdapter()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //Überprüfung des Zugriffes auf den aktuellen Standort
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Handler(Looper.getMainLooper()).postDelayed({
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), Constants.REQUEST_CODE
                )
            }, 5000)
        } else {
            mBinding.layoutMain.visibility = View.VISIBLE
            mBinding.lottieLoading.visibility = View.GONE
            // Standort verwenden
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    mainViewModel.getWeather(
                        // Jena Coordinator
                        if (location.latitude == 0.0) 50.927222 else location.latitude,
                        if (location.longitude == 0.0) 11.586111 else location.longitude,
                        BuildConfig.API_KEY
                    )
                } else {
                    mainViewModel.getWeather(
                        50.927222, 11.586111, BuildConfig.API_KEY
                    )
                }
            }
        }
        // Bei fehlender Internetverbindung werden die Daten aus der Datenbank abgerufen.
        if (!isNetwork(this)) {
            mainViewModel.getWeather()
            mainViewModel.getWeatherFiveDay()
        }

        // Die Datumsinformationen werden mit dem Datum des Telefons aktualisiert.

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Date()
        mBinding.textDate.text = dateFormat.format(currentDate)
        // Daten aus API
        mainViewModel.weatherLiveData.observe(this) { status ->
            when (status.getStatus()) {
                StateData.DataStatus.LOADING -> {}
                StateData.DataStatus.SUCCESS -> {
                    status.getData().let { weatherEntity ->
                        if (weatherEntity != null) {
                            temple = weatherEntity.main.temp?: 0.0
                            templeFeelLike = weatherEntity.main.feelsLike
                            val wind = DecimalFormat("#.#").format((weatherEntity.wind.speed))
                            val humidity =
                                DecimalFormat("#.#").format((weatherEntity.main.humidity))

                            val pressSure =
                                DecimalFormat("#.#").format((weatherEntity.main.pressure))

                            mBinding.textLocation.text = weatherEntity.name

                            mBinding.textFeelsLike.text =
                                "Feels like ${DecimalFormat("#.#").format((templeFeelLike - 273.15))}$tempExtensionC"

                            mBinding.textTemperature.text =
                                "${DecimalFormat("#.#").format((temple - 273.15))}$tempExtensionC"
                            mBinding.windSpeedIndex.text = "$wind$windExtensionF"
                            mBinding.humidityIndex.text = "$humidity$percentExtensions"

                            mBinding.pressureIndex.text = "$pressSure$percentExtensions"
                            // Verwenden Glide um Bilder im Bitmap-Format zu laden und anschließend in ein
                            // Array umzuwandeln, um sie in der Datenbank zu speichern.
                            Glide.with(this).asBitmap()
                                .load("${BuildConfig.BASE_GET_IMAGE}${weatherEntity.weather[0].icon}.$hpaExtensions")
                                .into(object : CustomTarget<Bitmap>() {
                                    override fun onResourceReady(
                                        resource: Bitmap, transition: Transition<in Bitmap>?
                                    ) {
                                        mBinding.imgWeather.setImageBitmap(resource)

                                        mainViewModel.insertWeather(
                                            WeatherDataBase(
                                                temp = temple,
                                                tempFeelsLike = templeFeelLike,
                                                humidity = weatherEntity.main.humidity,
                                                windSpeed = weatherEntity.wind.speed,
                                                pressure = weatherEntity.main.pressure,
                                                location = weatherEntity.name,
                                                imageWeather = bitmapToByteArray(resource)
                                            )
                                        )
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {

                                    }
                                })

                        }
                    }
                }

                StateData.DataStatus.ERROR -> {
                }

                else -> {

                }
            }
        }
        // 5 Tage Wetter Daten von API
        mainViewModel.weatherFiveDayLiveData.observe(this) { status ->
            if (status.getStatus() == StateData.DataStatus.SUCCESS) {
                status.getData().let { weatherEntity ->
                    if (weatherEntity != null) {

                        var dateDefault = ""

                        for (i in weatherEntity.list.indices){
                            val date = simpleDateFormatInput.parse(weatherEntity.list[i].dt_txt.toString())
                            if (dateDefault != simpleDateFormatOutput.format(date)){//
                                dateDefault = simpleDateFormatOutput.format(date)
                                arrWeatherDay.add(weatherEntity.list[i])
                            }
                        }

                        // Daten von 5 Tagen zur DataBase hinzufügen.
                        mainViewModel.insertFiveWeather(
                            WeatherFiveDayDataBase(
                                fiveDay = saveToDatabase(ArrayList(arrWeatherDay))
                            )
                        )
                    }

                    seeFiveDayAdapter?.setList(arrWeatherDay)
                }
            }
        }
        // Wenn kein Internet Verbindung, Daten wird von DataBase geladen
        mainViewModel.weatherDBLiveData.observe(this) { weatherDb ->
            if (weatherDb != null) {
                temple = weatherDb.temp!!
                templeFeelLike = weatherDb.tempFeelsLike!!
                val wind = DecimalFormat("#.#").format((weatherDb.windSpeed!!))
                val humidity = DecimalFormat("#.#").format((weatherDb.humidity!!))

                val pressSure = DecimalFormat("#.#").format((weatherDb.pressure!!))

                mBinding.textLocation.text = weatherDb.location

                mBinding.textFeelsLike.text =
                    "Feels like ${DecimalFormat("#.#").format((templeFeelLike - 273.15))}$tempExtensionC"

                mBinding.textTemperature.text =
                    "${DecimalFormat("#.#").format((temple - 273.15))}$tempExtensionC"
                mBinding.windSpeedIndex.text = "$wind$windExtensionF"
                mBinding.humidityIndex.text = "$humidity$percentExtensions"

                mBinding.pressureIndex.text = "$pressSure$hpaExtensions"

                val bitmap = BitmapFactory.decodeByteArray(
                    weatherDb.imageWeather, 0, weatherDb.imageWeather!!.size
                )

                mBinding.imgWeather.setImageBitmap(bitmap)
            }
        }
        //
        mainViewModel.weatherFiveDayDBLiveData.observe(this) {
            seeFiveDayAdapter?.setList(readFromDatabase(it.fiveDay.toString()))
        }
    }
     // Button klicken
    override fun bindViews() {
        super.bindViews()
        mBinding.lnChangeFC.setOnClickListener {
            isC = !isC

            mBinding.textTemperature.text =
                if (isC) "${DecimalFormat("#.#").format((temple - 273.15))}$tempExtensionC" else "${
                    DecimalFormat(
                        "#.#"
                    ).format(((temple - 273.15)*9/5 + 32))
                }$tempExtensionF"

            mBinding.textFeelsLike.text =
                if (isC) "Feels like ${DecimalFormat("#.#").format((templeFeelLike - 273.15))}$tempExtensionC" else "Feels like ${
                    DecimalFormat(
                        "#.#"
                    ).format(((temple - 273.15)*9/5 + 32))
                }$tempExtensionF"
        }

        mBinding.lnFiveDay.setOnClickListener {
            seeFiveDayAdapter?.let { it1 ->
                BtsDialogFiveDays(
                    this, mBinding.textLocation.text.toString(), it1
                ).show()
            }
        }
    }
    // Überprüfen ob die Berechtigung für den Zugriff auf den Standort erteilt wurde
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(
                        this, getString(R.string.permissionsDeny), Toast.LENGTH_LONG
                    ).show()
                    return
                }
                mBinding.layoutMain.visibility = View.VISIBLE
                mBinding.lottieLoading.visibility = View.GONE

                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        mainViewModel.getWeather(
                            location.latitude, location.longitude, BuildConfig.API_KEY
                        )
                    } else {
                        Toast.makeText(
                            this, getString(R.string.permissionsDeny), Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), Constants.REQUEST_CODE
                )
            }
        }
    }

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    fun saveToDatabase(weatherList: ArrayList<WeatherEntity>): String {
        val gson = Gson()
        return gson.toJson(weatherList)
    }

    fun readFromDatabase(fiveDayJson: String): ArrayList<WeatherEntity> {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<WeatherEntity>>() {}.type
        return gson.fromJson(fiveDayJson, type)
    }
}