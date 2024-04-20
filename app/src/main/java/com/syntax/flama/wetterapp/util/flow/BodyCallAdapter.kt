package com.syntax.flama.wetterapp.util.flow

import com.syntax.flama.wetterapp.util.exception.AppException
import com.syntax.flama.wetterapp.util.exception.ServerException
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.*
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ResponseCallAdapter<T>(
    private val responseType: Type
) : CallAdapter<T, Flow<T>> {
    override fun adapt(call: Call<T>): Flow<T> {
        return flow {
            emit(
                suspendCancellableCoroutine { continuation ->
                    call.enqueue(FlowCallback(continuation))
                    continuation.invokeOnCancellation { call.cancel() }
                }
            )
        }
    }

    override fun responseType() = responseType
}

private class FlowCallback<T>(private val continuation: CancellableContinuation<T>) : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful.not()) {
            continuation.resumeWithException(
                AppException(
                    message = "Something Wrong: request not success",
                    cause = java.lang.NullPointerException()
                )
            )
            return
        }
        val body = response.body()
        if (body == null) {
            continuation.resumeWithException(
                AppException(
                    message = "Something Wrong: body is null",
                    cause = java.lang.NullPointerException()
                )
            )
            return
        } else {
            continuation.resume(body)
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        continuation.resumeWithException(ServerException(message = t.message, cause = t.cause))
    }
}

class BodyCallAdapter<T>(private val responseType: Type) : CallAdapter<T, Flow<T>> {
    override fun adapt(call: Call<T>): Flow<T> {
        return flow {
            emit(
                suspendCancellableCoroutine { continuation ->
                    call.enqueue(FlowCallback(continuation))
                    continuation.invokeOnCancellation { call.cancel() }
                }
            )
        }
    }

    override fun responseType() = responseType


    fun Call<T>.createNullPointerError(): java.lang.NullPointerException {
        val invocation = this.request().tag(Invocation::class.java) ?: return NullPointerException()
        val method = invocation.method()
        return KotlinNullPointerException("Response from " + method.declaringClass.name + " - " + method.name)
    }
}