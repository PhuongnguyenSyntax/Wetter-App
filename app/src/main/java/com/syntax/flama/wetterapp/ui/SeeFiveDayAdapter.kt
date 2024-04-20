package com.syntax.flama.wetterapp.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.syntax.flama.wetterapp.BuildConfig
import com.syntax.flama.wetterapp.R
import com.syntax.flama.wetterapp.data.apiEntity.WeatherEntity
import com.syntax.flama.wetterapp.databinding.ItemFiveDayBinding
import com.syntax.flama.wetterapp.util.Constants
import java.text.DecimalFormat

class SeeFiveDayAdapter : RecyclerView.Adapter<SeeFiveDayAdapter.ViewHolder>() {
    var arrWeather = mutableListOf<WeatherEntity>()

    fun setList(newList: List<WeatherEntity>) {
        arrWeather.clear()
        arrWeather.addAll(newList)
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemFiveDayBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        arrWeather.getOrNull(position)?.let { holder.onBinding(it) }
    }

    override fun getItemCount(): Int {
        return arrWeather.size
    }

    inner class ViewHolder(private val binding: ItemFiveDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun onBinding(data: WeatherEntity) {
            val temple = DecimalFormat("#.#").format(((data.main.temp?:0.0) - 273.15))

            binding.tvTime.text = data.dt_txt

            binding.tvStatus.text = data.weather[0].description

            Glide.with(binding.root.context)
                .load("${BuildConfig.BASE_GET_IMAGE}${data.weather[0].icon}.${Constants.pngExtensions}")
                .into(binding.imgWeather)
            binding.tvTemp.text = binding.root.context.getString(R.string.template, temple)
        }
    }
}
