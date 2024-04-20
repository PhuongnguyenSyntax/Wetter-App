package com.syntax.flama.wetterapp.data.liveData

class StateData<T> {

    private var status: DataStatus = DataStatus.CREATED

    private var data: T? = null

    private var errorData: T? = null

    private var errorMsg: String? = null

    fun loading(): StateData<T> {
        status = DataStatus.LOADING
        data = null
        errorMsg = null
        return this
    }

    fun success(data: T): StateData<T> {
        status = DataStatus.SUCCESS
        this.data = data
        errorMsg = null
        return this
    }

    fun error(errorMsg: String): StateData<T> {
        status = DataStatus.ERROR
        data = null
        this.errorMsg = errorMsg
        return this
    }

    fun error(errorData: T): StateData<T> {
        status = DataStatus.ERROR
        data = null
        this.errorData = errorData
        return this
    }

    fun getStatus(): DataStatus {
        return status
    }

    fun getData(): T? {
        return data
    }


    fun getErrorMsg(): String? {
        return errorMsg
    }


    fun getErrorData(): T? {
        return errorData
    }

    enum class DataStatus {
        CREATED, SUCCESS, ERROR, LOADING
    }

}
