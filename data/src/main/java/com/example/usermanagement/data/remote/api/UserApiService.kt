package com.example.usermanagement.data.remote.api

import com.example.usermanagement.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API interface for user-related endpoints.
 * All endpoints return Response<T> to handle HTTP errors properly.
 */
interface UserApiService {

    /**
     * Get paginated list of users
     */
    @GET("users")
    suspend fun getUsers(
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int
    ): Response<PaginatedResponse<UserDto>>

    /**
     * Get user by ID
     */
    @GET("users/{id}")
    suspend fun getUserById(
        @Path("id") userId: String
    ): Response<BaseResponse<UserDto>>

    /**
     * Create a new user
     */
    @POST("users")
    suspend fun createUser(
        @Body userDto: CreateUserDto
    ): Response<BaseResponse<UserDto>>

    /**
     * Update an existing user
     */
    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") userId: String,
        @Body userDto: UpdateUserDto
    ): Response<BaseResponse<UserDto>>

    /**
     * Delete a user
     */
    @DELETE("users/{id}")
    suspend fun deleteUser(
        @Path("id") userId: String
    ): Response<BaseResponse<Unit>>
}
