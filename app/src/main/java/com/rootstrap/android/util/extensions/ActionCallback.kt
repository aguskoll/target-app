package com.rootstrap.android.util.extensions

import com.google.gson.Gson
import com.rootstrap.android.network.models.ErrorModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response

class ActionCallback {

    companion object {

        const val FULL_ERROR_MESSAGES = "full_messages"

        suspend fun <T> call(apiCall: Call<T>): Result<Data<T>> =
            withContext(Dispatchers.IO) {
                val response = apiCall.execute()
                handleResponse(response)
            }

        private fun <T> handleResponse(response: Response<T>): Result<Data<T>> {
            return if (response.isSuccessful) {
                Result.success(
                    Data(response.body())
                )
            } else {
                handleResponseError(response)
            }
        }

        private fun <T> handleResponseError(response: Response<T>): Result<Data<T>> {
            try {
                var errorMessage = ""

                response.errorBody()?.let {
                    val apiError = Gson().fromJson(it.charStream(), ErrorModel::class.java)

                    if (apiError.errors is Map<*, *> && apiError.errors[FULL_ERROR_MESSAGES] is ArrayList<*>) {
                        val results: ArrayList<*> =
                            apiError.errors[FULL_ERROR_MESSAGES] as ArrayList<*>

                        errorMessage = if (results.isNotEmpty() && results.first() is String) {
                            results.first() as String
                        } else apiError.error ?: ""
                    }

                    return Result.failure(
                        ApiException(
                            errorMessage = errorMessage
                        )
                    )
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            return Result.failure(ApiException(errorType = ApiErrorType.unknownError))
        }
    }
}

class Data<T>(val value: T?)

class ApiException(
    private val errorMessage: String? = null,
    val errorType: ApiErrorType = ApiErrorType.apiError
) : java.lang.Exception() {
    override val message: String?
        get() = errorMessage
}

enum class ApiErrorType {
    apiError,
    unknownError
}
