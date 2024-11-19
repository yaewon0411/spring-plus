package org.example.expert.util.api

import org.aspectj.bridge.Message

data class ApiResult<T>(
    val success: Boolean,
    val data: T?,
    val apiError: ApiError?
) {
    companion object{
        fun <T> success(data: T): ApiResult<T> = ApiResult(
            success = true,
            data = data,
            apiError = null
        )

        fun <T> error(status: Int, message: String): ApiResult<T> = ApiResult(
            success = false,
            data = null,
            apiError = ApiError(status = status, message = message)
        )

        fun<T> error(status: Int, message: String, data: T):ApiResult<T> = ApiResult(
            success = false,
            data = data,
            apiError = ApiError(status = status, message = message)
        )
    }
}

/*
*
* data
* apiError
*
*
* */