package com.syntax.flama.wetterapp.util.flow

import android.util.Log
import com.syntax.flama.wetterapp.data.liveData.MutableStateLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart

suspend fun <T> Flow<T>.collectAsSateLiveData(liveData: MutableStateLiveData<T>) {
    this.flowOn(Dispatchers.IO)
        .catch {
            Log.e("error", "catch: ${Thread.currentThread().name}")
            Log.e("error", "catch: ${it.message}")
            liveData.postError(it.message)
        }
        .onStart {
            liveData.postLoading()
        }.collect {
            liveData.postSuccess(it)
        }
}