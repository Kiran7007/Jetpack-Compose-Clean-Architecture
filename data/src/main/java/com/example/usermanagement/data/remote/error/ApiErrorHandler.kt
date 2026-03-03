package com.example.usermanagement.data.remote.error

import com.example.usermanagement.core.DomainException
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Error response model from API
 */
data class ApiError(
    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("message")
    val message: String,

    @SerializedName("errors")
    val errors: Map<String, String>? = null,

    @SerializedName("code")
    val code: Int? = null
)

/**
 * Handles API errors and converts them to domain exceptions.
 * This is a critical part that ensures infrastructure errors don't leak into domain.
 */
object ApiErrorHandler {

    private val gson = Gson()

    /**
     * Handle Retrofit Response and convert to DomainException if error
     */
    fun <T> handleApiError(response: Response<T>): DomainException {
        return when (response.code()) {
            400 -> {
                val apiError = parseErrorBody(response)
                DomainException.ValidationException(
                    errors = apiError?.errors ?: emptyMap(),
                    message = apiError?.message ?: "Bad request"
                )
            }
            401 -> DomainException.UnauthorizedException(
                message = parseErrorMessage(response) ?: "Unauthorized"
            )
            404 -> DomainException.NotFoundException(
                message = parseErrorMessage(response) ?: "Resource not found"
            )
            408 -> DomainException.TimeoutException(
                message = "Request timeout"
            )
            in 500..599 -> DomainException.ServerException(
                code = response.code(),
                message = parseErrorMessage(response) ?: "Server error"
            )
            else -> DomainException.UnknownException(
                message = parseErrorMessage(response) ?: "Unknown error occurred"
            )
        }
    }

    /**
     * Handle exceptions from network calls
     */
    fun handleException(exception: Throwable): DomainException {
        return when (exception) {
            is UnknownHostException -> DomainException.NoInternetException()
            is SocketTimeoutException -> DomainException.TimeoutException()
            is IOException -> DomainException.NetworkException(
                message = exception.message ?: "Network error"
            )
            is DomainException -> exception
            else -> DomainException.UnknownException(
                message = exception.message ?: "Unknown error",
                cause = exception
            )
        }
    }

    /**
     * Parse error body from response
     */
    private fun <T> parseErrorBody(response: Response<T>): ApiError? {
        return try {
            val errorBody = response.errorBody()?.string()
            if (errorBody != null) {
                gson.fromJson(errorBody, ApiError::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Parse error message from response
     */
    private fun <T> parseErrorMessage(response: Response<T>): String? {
        return parseErrorBody(response)?.message ?: response.message()
    }
}

/**
 * Extension function to safely handle API calls
 */
suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<T>
): T {
    try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                return body
            } else {
                throw DomainException.UnknownException("Response body is null")
            }
        } else {
            throw ApiErrorHandler.handleApiError(response)
        }
    } catch (e: DomainException) {
        throw e
    } catch (e: Exception) {
        throw ApiErrorHandler.handleException(e)
    }
}
