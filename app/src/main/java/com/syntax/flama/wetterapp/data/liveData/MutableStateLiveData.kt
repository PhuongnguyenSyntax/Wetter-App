package com.syntax.flama.wetterapp.data.liveData

import androidx.lifecycle.MutableLiveData


class MutableStateLiveData<T> : MutableLiveData<StateData<T>>() {
    var currentSate = StateData.DataStatus.CREATED

    fun postLoading() {
        postValue(StateData<T>().loading())
        currentSate = StateData.DataStatus.LOADING
    }

    fun postError(errorMsg: String?) {
        postValue(StateData<T>().error(errorMsg ?: ""))
        currentSate = StateData.DataStatus.ERROR
    }

    fun postErrorData(errorData: T) {
        postValue(StateData<T>().error(errorData))
        currentSate = StateData.DataStatus.ERROR
    }

    fun postSuccess(data: T) {
        postValue(StateData<T>().success(data))
        currentSate = StateData.DataStatus.SUCCESS
    }
}